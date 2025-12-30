package net.rollanddeath.smp.core.scripting;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.scope.ScopeContainer;
import net.rollanddeath.smp.core.scripting.scope.ScopeId;
import net.rollanddeath.smp.core.scripting.scope.EventPayloads;
import net.rollanddeath.smp.core.scripting.scope.ScopePath;
import net.rollanddeath.smp.core.scripting.scope.modifiers.ScopedModifierEngine;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

public final class ScriptContext {

    private final RollAndDeathSMP plugin;
    private final Player player;
    private final String subjectId;
    private final ScriptPhase phase;
    private final Map<String, Object> variables;

    // Scopes: SUBJECT, TARGET, PROJECTILE, PLAYER, WORLD, CHUNK, GLOBAL, TEAM, EVENT
    private final EnumMap<ScopeId, ScopeContainer> scopes;

    public ScriptContext(RollAndDeathSMP plugin, Player player, String subjectId, ScriptPhase phase, Map<String, Object> variables) {
        this.plugin = plugin;
        this.player = player;
        this.subjectId = subjectId;
        this.phase = phase;
        this.variables = variables;

        this.scopes = new EnumMap<>(ScopeId.class);
        initScopes();

        // Aplica scoped_modifiers (solo set_var/add_var y solo sobre *.custom/*.generic).
        if (plugin != null && plugin.getScopeRegistry() != null) {
            ScopedModifierEngine.applyAll(this, plugin.getScopeRegistry().scopedModifiers());
        }
    }

    private void initScopes() {
        if (plugin == null) return;

        // GLOBAL siempre existe
        scopes.put(ScopeId.GLOBAL, plugin.getScopeRegistry().global());

        // Normalización cross-módulo: SUBJECT/TARGET/PROJECTILE SOLO desde variables internas.
        // Importante: no usamos caster/target/projectile legacy (el engine ya no debe inyectarlas).
        Object subjectBase = variables != null ? variables.get("__subject") : null;
        Object targetBase = variables != null ? variables.get("__target") : null;
        Object projectileBase = variables != null ? variables.get("__projectile") : null;
        Object itemBase = variables != null ? variables.get("__item") : null;

        if (subjectBase instanceof Entity) {
            scopes.put(ScopeId.SUBJECT, plugin.getScopeRegistry().subject(subjectBase));
        }
        if (targetBase instanceof Entity) {
            scopes.put(ScopeId.TARGET, plugin.getScopeRegistry().target(targetBase));
        }
        if (projectileBase instanceof Entity) {
            scopes.put(ScopeId.PROJECTILE, plugin.getScopeRegistry().projectile(projectileBase));
        }

        if (itemBase instanceof ItemStack is) {
            scopes.put(ScopeId.ITEM, plugin.getScopeRegistry().item(is));
        }

        if (player != null) {
            scopes.put(ScopeId.PLAYER, plugin.getScopeRegistry().player(player));

            // En la mayoría de eventos de jugador, el SUBJECT equivale al propio player.
            if (!scopes.containsKey(ScopeId.SUBJECT)) {
                scopes.put(ScopeId.SUBJECT, plugin.getScopeRegistry().subject(player));
            }

            // PLAYER.state.isInCombat (si el combat log está activo)
            try {
                var cl = plugin.getCombatLogManager();
                if (cl != null && cl.isEnabled()) {
                    boolean inCombat = cl.getRemainingCombatSeconds(player.getUniqueId()) > 0;
                    scopes.get(ScopeId.PLAYER).setStateEngineOnly("PLAYER.state.isInCombat", inCombat);
                }
            } catch (Exception ignored) {
            }

            if (player.getWorld() != null) {
                scopes.put(ScopeId.WORLD, plugin.getScopeRegistry().world(player.getWorld()));
            }

            try {
                scopes.put(ScopeId.CHUNK, plugin.getScopeRegistry().chunk(player.getLocation().getChunk()));
            } catch (Exception ignored) {
            }

            try {
                if (plugin.getTeamManager() != null) {
                    var team = plugin.getTeamManager().getTeam(player.getUniqueId());
                    if (team != null) {
                        scopes.put(ScopeId.TEAM, plugin.getScopeRegistry().team(team));
                    }
                }
            } catch (Exception ignored) {
            }
        }

        // Si no hay player, intentamos derivar WORLD/CHUNK del SUBJECT/TARGET.
        if (!scopes.containsKey(ScopeId.WORLD) || !scopes.containsKey(ScopeId.CHUNK)) {
            World w = null;
            Chunk ch = null;

            try {
                if (subjectBase instanceof Entity ce) {
                    w = ce.getWorld();
                    ch = ce.getLocation() != null ? ce.getLocation().getChunk() : null;
                }
            } catch (Exception ignored) {
            }

            if (w == null || ch == null) {
                try {
                    if (targetBase instanceof Entity te) {
                        if (w == null) w = te.getWorld();
                        if (ch == null) ch = te.getLocation() != null ? te.getLocation().getChunk() : null;
                    }
                } catch (Exception ignored) {
                }
            }

            if (!scopes.containsKey(ScopeId.WORLD) && w != null) {
                scopes.put(ScopeId.WORLD, plugin.getScopeRegistry().world(w));
            }
            if (!scopes.containsKey(ScopeId.CHUNK) && ch != null) {
                scopes.put(ScopeId.CHUNK, plugin.getScopeRegistry().chunk(ch));
            }
        }

        Object eventRaw = variables != null ? variables.get("__event") : null;
        Object eventPayload = EventPayloads.wrap(eventRaw);
        ScopeContainer eventScope = plugin.getScopeRegistry().event(eventPayload);
        scopes.put(ScopeId.EVENT, eventScope);

        // ITEM (fallback): si no se inyectó __item, intentamos derivarlo desde EVENT.item.
        // Esto cubre eventos Bukkit que exponen getItem() y payloads custom que ya incluyen "item".
        if (!scopes.containsKey(ScopeId.ITEM)) {
            try {
                ItemStack fromEvent = null;
                if (eventPayload instanceof java.util.Map<?, ?> m) {
                    Object it = m.get("item");
                    if (it instanceof ItemStack is) {
                        fromEvent = is;
                    }
                }
                if (fromEvent != null) {
                    scopes.put(ScopeId.ITEM, plugin.getScopeRegistry().item(fromEvent));
                }
            } catch (Exception ignored) {
            }
        }

        // Metadata estable para que builtins/scripts no dependan de nombres legacy por módulo.
        try {
            if (subjectId != null) {
                eventScope.setCacheEngineOnly("EVENT.meta.subjectId", subjectId);
            }
            if (phase != null) {
                eventScope.setCacheEngineOnly("EVENT.meta.phase", phase.name());
            }
        } catch (Exception ignored) {
        }

        // EVENT.state.* (solo lectura para scripts)
        try {
            boolean isAsync = eventRaw instanceof org.bukkit.event.Event e && e.isAsynchronous();
            eventScope.setStateEngineOnly("EVENT.state.isAsync", isAsync);

            boolean isCancelled = false;
            if (eventRaw instanceof org.bukkit.event.Cancellable c) {
                isCancelled = c.isCancelled();
            }
            eventScope.setStateEngineOnly("EVENT.state.isCancelled", isCancelled);

            // isHandled: flag libre del engine (por defecto false)
            eventScope.setStateEngineOnly("EVENT.state.isHandled", false);
        } catch (Exception ignored) {
        }

        // LOCATION: intentamos derivarlo (en orden) desde EVENT.location/to/from y si no desde SUBJECT.location.
        // Esto permite que scripts usen LOCATION.x/y/z/world de forma consistente.
        try {
            Location loc = null;
            if (eventPayload instanceof Map<?, ?> m) {
                Object l = m.get("location");
                if (l instanceof Location ll) loc = ll;
                if (loc == null) {
                    Object t = m.get("to");
                    if (t instanceof Location tl) loc = tl;
                }
                if (loc == null) {
                    Object f = m.get("from");
                    if (f instanceof Location fl) loc = fl;
                }
            }
            if (loc == null && subjectBase instanceof Entity se) {
                loc = se.getLocation();
            }
            if (loc != null) {
                scopes.put(ScopeId.LOCATION, plugin.getScopeRegistry().location(loc));

                // Si aún falta WORLD/CHUNK, también los derivamos desde LOCATION.
                if (!scopes.containsKey(ScopeId.WORLD) && loc.getWorld() != null) {
                    scopes.put(ScopeId.WORLD, plugin.getScopeRegistry().world(loc.getWorld()));
                }
                if (!scopes.containsKey(ScopeId.CHUNK)) {
                    try {
                        scopes.put(ScopeId.CHUNK, plugin.getScopeRegistry().chunk(loc.getChunk()));
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception ignored) {
        }

        // No copiamos variables legacy dentro de EVENT.*.
        // Objetivo: eliminar el sistema sin scopes; EVENT.native/state + scopes de entidades son la fuente de verdad.
    }

    public RollAndDeathSMP plugin() {
        return plugin;
    }

    public Player player() {
        return player;
    }

    public String subjectId() {
        return subjectId;
    }

    public ScriptPhase phase() {
        return phase;
    }

    public Map<String, Object> variables() {
        return variables;
    }

    /**
     * Acceso directo a los scopes para operaciones avanzadas del engine.
     * Preferir usar métodos tipados como subject(), player(), getValue() cuando sea posible.
     */
    public EnumMap<ScopeId, ScopeContainer> scopes() {
        return scopes;
    }

    /**
     * Base del scope SUBJECT si está disponible (incluye fallback a PLAYER cuando aplica).
     * Preferir este método sobre acceder a {@code __subject} directo.
     */
    public Entity subject() {
        Object base = base(ScopeId.SUBJECT);
        return base instanceof Entity e ? e : null;
    }

    public Entity target() {
        Object base = base(ScopeId.TARGET);
        return base instanceof Entity e ? e : null;
    }

    public Entity projectile() {
        Object base = base(ScopeId.PROJECTILE);
        return base instanceof Entity e ? e : null;
    }

    public ItemStack item() {
        Object base = base(ScopeId.ITEM);
        return base instanceof ItemStack is ? is : null;
    }

    public Location location() {
        Object base = base(ScopeId.LOCATION);
        return base instanceof Location l ? l : null;
    }

    public World world() {
        Object base = base(ScopeId.WORLD);
        return base instanceof World w ? w : null;
    }

    public Chunk chunk() {
        Object base = base(ScopeId.CHUNK);
        return base instanceof Chunk c ? c : null;
    }

    public Object base(ScopeId id) {
        if (id == null) return null;
        ScopeContainer sc = scopes.get(id);
        return sc != null ? sc.base() : null;
    }

    public <T> T base(ScopeId id, Class<T> type) {
        if (type == null) return null;
        Object b = base(id);
        return type.isInstance(b) ? type.cast(b) : null;
    }

    /**
     * Payload estable del scope EVENT (normalizado por {@link EventPayloads#wrap(Object)}).
     *
     * Importante: esto NO tiene por qué ser un Bukkit Event.
     * Es el objeto que alimenta rutas como EVENT.location/EVENT.entity/EVENT.item/etc.
     */
    public Object event() {
        return eventPayload();
    }

    /**
     * Evento nativo original (típicamente un Bukkit Event) cuando existe.
     *
     * Si {@code __event} es un Map sin native/__native, devuelve null.
     */
    public Object nativeEvent() {
        Object raw = eventRaw();
        if (raw instanceof java.util.Map<?, ?> m) {
            if (m.containsKey("native")) return m.get("native");
            if (m.containsKey("__native")) return m.get("__native");
            return null;
        }
        return raw;
    }

    public <T> T nativeEvent(Class<T> type) {
        if (type == null) return null;
        Object ev = nativeEvent();
        return type.isInstance(ev) ? type.cast(ev) : null;
    }

    /** Devuelve exactamente lo que se inyectó como {@code __event} (wrapper/payload/raw). */
    public Object eventRaw() {
        return variables != null ? variables.get("__event") : null;
    }

    /** Acceso al wrapper Map interno de {@code __event} si aplica. */
    @SuppressWarnings("unchecked")
    public Map<String, Object> eventMap() {
        Object payload = eventPayload();
        if (payload instanceof Map<?, ?>) {
            return (Map<String, Object>) payload;
        }
        return null;
    }

    /** Acceso tipado al payload de EVENT. */
    public <T> T event(Class<T> type) {
        if (type == null) return null;
        Object p = event();
        return type.isInstance(p) ? type.cast(p) : null;
    }

    public <T> T subject(Class<T> type) {
        return base(ScopeId.SUBJECT, type);
    }

    public <T> T target(Class<T> type) {
        return base(ScopeId.TARGET, type);
    }

    public <T> T projectile(Class<T> type) {
        return base(ScopeId.PROJECTILE, type);
    }

    public <T> T item(Class<T> type) {
        return base(ScopeId.ITEM, type);
    }

    public <T extends Entity> T eventEntity(Class<T> type) {
        if (type == null) return null;
        org.bukkit.event.entity.EntityEvent ee = nativeEvent(org.bukkit.event.entity.EntityEvent.class);
        if (ee == null) return null;
        Entity e = ee.getEntity();
        return type.isInstance(e) ? type.cast(e) : null;
    }

    public <T extends Entity> T subjectOrEventEntity(Class<T> type) {
        if (type == null) return null;
        T s = subject(type);
        return s != null ? s : eventEntity(type);
    }

    /** Base del scope EVENT (payload ya envuelto por {@link EventPayloads#wrap(Object)}). */
    public Object eventPayload() {
        return base(ScopeId.EVENT);
    }

    public String stringVar(String key) {
        Object v = getValue(key);
        return v != null ? String.valueOf(v) : null;
    }

    public ScopeContainer scope(ScopeId id) {
        return scopes.get(id);
    }

    public Object getValue(String keyOrPath) {
        if (keyOrPath == null) return null;

        // Acceso engine-only a la key interna __event (para builtins).
        // Nota: si __event es un Map con native/__native, devolvemos el evento nativo.
        String rawInternal = keyOrPath.trim();
        if (rawInternal.equals("__event")) {
            Object ev = variables != null ? variables.get("__event") : null;
            if (ev instanceof java.util.Map<?, ?> m) {
                if (m.containsKey("native")) return m.get("native");
                if (m.containsKey("__native")) return m.get("__native");
            }
            return ev;
        }

        // Tokens de scope (sin ".") para casos donde el YAML necesita un objeto base (Entity/Location/etc).
        // Ej: where: SUBJECT / center: TARGET / shooter_key: PROJECTILE
        String raw = rawInternal;
        if (!raw.isEmpty() && raw.indexOf('.') < 0) {
            try {
                ScopeId token = ScopeId.valueOf(raw.toUpperCase(java.util.Locale.ROOT));
                ScopeContainer sc = scopes.get(token);
                if (sc != null && sc.base() != null) return sc.base();
            } catch (IllegalArgumentException ignored) {
            }

            // Compat controlada: si el YAML pasa una key sin scope, la interpretamos como EVENT.<key>.
            // Mantiene el contrato de scopes (EVENT), pero evita romper configs antiguas (especialmente modifiers).
            try {
                String candidate = "EVENT." + raw;
                ScopePath p2 = ScopePath.parse(candidate);
                if (p2 != null) {
                    ScopeContainer sc2 = scopes.get(p2.scope());
                    Object v2 = sc2 != null ? sc2.get(p2) : null;
                    if (v2 != null) return v2;
                }
            } catch (Exception ignored) {
            }
        }

        ScopePath p = ScopePath.parse(keyOrPath);
        if (p != null) {
            ScopeContainer sc = scopes.get(p.scope());
            return sc != null ? sc.get(p) : null;
        }

        return null;
    }

    public void setGenericVar(String scopedPath, Object value) {
        ScopePath p = ScopePath.parse(scopedPath);
        if (p == null) {
            throw new net.rollanddeath.smp.core.scripting.scope.ScriptAccessException(scopedPath, "Path inválido: " + scopedPath);
        }
        ScopeContainer sc = scopes.get(p.scope());
        if (sc == null) {
            throw new net.rollanddeath.smp.core.scripting.scope.ScriptAccessException(scopedPath, "Scope no disponible en este contexto: " + p.scope());
        }
        sc.setGeneric(scopedPath, value);
    }

    /**
     * Escritura "compatible" para reducir boilerplate:
     * - Si el path ya es un scoped path válido (ej: PLAYER.generic.foo, EVENT.custom.bar), se usa tal cual.
     * - Si NO tiene '.', se interpreta como EVENT.custom.<key>.
     *
     * Objetivo: permitir keys temporales como "ak_curve_side" sin romper el contrato de scopes.
     */
    public void setGenericVarCompat(String keyOrScopedPath, Object value) {
        String p = normalizeWritePath(keyOrScopedPath);
        if (p == null) return;
        setGenericVar(p, value);
    }

    /**
     * Igual que {@link #putCacheVarEngineOnly(String, Object)} pero aceptando keys sin scope.
     */
    public void putCacheVarEngineOnlyCompat(String keyOrScopedPath, Object value) {
        String p = normalizeWritePath(keyOrScopedPath);
        if (p == null) return;
        putCacheVarEngineOnly(p, value);
    }

    private static String normalizeWritePath(String keyOrScopedPath) {
        if (keyOrScopedPath == null) return null;
        String raw = keyOrScopedPath.trim();
        if (raw.isBlank()) return null;

        // Si ya parece scoped (tiene '.') y parsea, lo respetamos.
        if (raw.indexOf('.') >= 0) {
            try {
                ScopePath p = ScopePath.parse(raw);
                if (p != null) return raw;
            } catch (Exception ignored) {
            }
        }

        // Si no tiene scope, lo enviamos a EVENT.custom.*
        return "EVENT.custom." + raw;
    }

    public void putCacheVarEngineOnly(String scopedPath, Object value) {
        ScopePath p = ScopePath.parse(scopedPath);
        if (p == null) {
            throw new net.rollanddeath.smp.core.scripting.scope.ScriptAccessException(scopedPath, "Path inválido: " + scopedPath);
        }
        ScopeContainer sc = scopes.get(p.scope());
        if (sc == null) {
            throw new net.rollanddeath.smp.core.scripting.scope.ScriptAccessException(scopedPath, "Scope no disponible en este contexto: " + p.scope());
        }
        sc.setCacheEngineOnly(scopedPath, value);
    }
}
