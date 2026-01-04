package net.rollanddeath.smp.core.scripting;

import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.core.scripting.builtin.actions.ActionRegistrar;
import net.rollanddeath.smp.integration.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.bukkit.entity.LivingEntity;
import net.rollanddeath.smp.core.scripting.Action;

public final class Resolvers {

    private Resolvers() {
    }

    /**
     * Devuelve el valor tal cual, sin resolverlo ni interpolarlo. Útil para capturar specs crudas
     * en el parseo y resolverlas más adelante con el ScriptContext.
     */
    public static Object plain(Object value) {
        return value;
    }

    public static Object plain(Map<?, ?> raw, String... keys) {
        return firstObject(raw, keys);
    }

    public static Object resolve(ScriptContext ctx, Object value) {
        if (value instanceof String s) {
            String trimmed = s.trim();
            // Interpolación ${...} (puede mezclar texto y placeholders)
            Object interpolated = interpolate(ctx, s);
            if (interpolated != null) return interpolated;

            // Resolución implícita de claves simples sin ${}
            // Ej: "EVENT.custom.damage" debe poder venir en crudo desde YAML antiguos.
            if (ctx != null) {
                Object ctxVal = ctx.getValue(trimmed);
                if (ctxVal != null) {
                    return ctxVal;
                }
            }

            return value;
        }
        return value;
    }

    public static List<String> stringList(Map<?, ?> raw, String... keys) {
        Object val = firstObject(raw, keys);
        if (val instanceof List<?> list) {
            return list.stream().map(String::valueOf).collect(Collectors.toList());
        }
        if (val instanceof String s) {
            return List.of(s);
        }
        return List.of();
    }

    public static String string(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return string(ctx, firstObject(raw, keys));
    }

    public static Integer integer(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return integer(ctx, firstObject(raw, keys));
    }

    public static Long longVal(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return longVal(ctx, firstObject(raw, keys));
    }

    public static Double doubleVal(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return doubleVal(ctx, firstObject(raw, keys));
    }
    
    public static Boolean bool(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return bool(ctx, firstObject(raw, keys));
    }

    public static String string(ScriptContext ctx, Object value) {
        if (value == null) return null;
        Object resolved = resolve(ctx, value);
        if (resolved == null) return null;

        String s = String.valueOf(resolved);
        if (ctx != null && ctx.player() != null) {
            return PlaceholderUtil.resolvePlaceholders(ctx.plugin(), ctx.player(), s);
        }
        return s;
    }

    public static Integer integer(ScriptContext ctx, Object value) {
        if (value == null) return null;
        Object resolved = resolve(ctx, value);

        if (resolved instanceof Number n) return n.intValue();
        if (resolved instanceof String s) {
            String t = s.trim();
            if (t.isEmpty()) return null;
            try {
                return Integer.parseInt(t);
            } catch (Exception ignored) {
                // fallthrough
            }
        }
        return null;
    }

    public static Long longVal(ScriptContext ctx, Object value) {
        if (value == null) return null;
        Object resolved = resolve(ctx, value);

        if (resolved instanceof Number n) return n.longValue();
        if (resolved instanceof String s) {
            String t = s.trim();
            if (t.isEmpty()) return null;
            try {
                return Long.parseLong(t);
            } catch (Exception ignored) {
                // fallthrough
            }
        }
        return null;
    }

    public static Double doubleVal(ScriptContext ctx, Object value) {
        if (value == null) return null;
        Object resolved = resolve(ctx, value);

        if (resolved instanceof Number n) return n.doubleValue();
        if (resolved instanceof String s) {
            String t = s.trim();
            if (t.isEmpty()) return null;
            try {
                return Double.parseDouble(t);
            } catch (Exception ignored) {
                // fallthrough
            }
        }
        return null;
    }

    public static Boolean bool(ScriptContext ctx, Object value) {
        if (value == null) return null;
        Object resolved = resolve(ctx, value);

        if (resolved instanceof Boolean b) return b;
        if (resolved instanceof Number n) return n.intValue() != 0;
        if (resolved instanceof String s) {
            String v = s.trim().toLowerCase(Locale.ROOT);
            if (v.isEmpty()) return null;
            if (Objects.equals(v, "true") || Objects.equals(v, "1") || Objects.equals(v, "yes") || Objects.equals(v, "on")) return true;
            if (Objects.equals(v, "false") || Objects.equals(v, "0") || Objects.equals(v, "no") || Objects.equals(v, "off")) return false;
        }
        return null;
    }

    public static Object object(ScriptContext ctx, Object value) {
        return value != null ? resolve(ctx, value) : null;
    }

    public static Object object(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return object(ctx, firstObject(raw, keys));
    }

    public static Location location(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return location(ctx, firstObject(raw, keys), ctx != null && ctx.player() != null ? ctx.player().getWorld() : null);
    }

    public static Location location(ScriptContext ctx, Object spec) {
        return location(ctx, spec, ctx != null && ctx.player() != null ? ctx.player().getWorld() : null);
    }

    public static Location location(ScriptContext ctx, Object spec, World defaultWorld) {
        if (spec == null) return null;

        Object resolved = resolve(ctx, spec);
        if (resolved != null) {
            spec = resolved;
        }

        // Direct entities / UUIDs first
        if (spec instanceof Entity e) {
            Location l = coerceLocation(e);
            if (l != null) return l;
        }
        if (spec instanceof UUID id) {
            try {
                Entity e = Bukkit.getEntity(id);
                Location l = coerceLocation(e);
                if (l != null) return l;
            } catch (Exception ignored) {
                // fallthrough
            }
        }

        // String key/ref or inline coords
        if (spec instanceof String s) {
            String t = s.trim();
            if (t.isEmpty()) return null;

            Object ctxVal = ctx != null ? ctx.getValue(t) : null;
            Location fromVar = coerceLocation(ctxVal);
            if (fromVar != null) return fromVar;

            Entity ent = entity(ctx, t);
            Location fromEnt = coerceLocation(ent);
            if (fromEnt != null) return fromEnt;

            String[] parts = t.split(",");
            if (parts.length == 3) {
                try {
                    double x = Double.parseDouble(parts[0].trim());
                    double y = Double.parseDouble(parts[1].trim());
                    double z = Double.parseDouble(parts[2].trim());
                    World w = defaultWorld != null ? defaultWorld : (ctx != null && ctx.player() != null ? ctx.player().getWorld() : null);
                    if (w != null) return new Location(w, x, y, z);
                } catch (Exception ignored) {
                    // fallthrough
                }
            }

            return null;
        }

        // Direct Location/Entity/Block
        Location direct = coerceLocation(spec);
        if (direct != null) return direct;

        // Inline map
        if (spec instanceof Map<?, ?> map) {
            Object keyObj = firstObject(map, "key", "var", "ref");
            if (keyObj != null) {
                Location byKey = location(ctx, keyObj, defaultWorld);
                if (byKey != null) return byKey;
            }

            Object entObj = firstObject(map, "entity", "entity_key", "entity_ref", "target", "caster", "uuid", "id", "name", "player");
            if (entObj != null) {
                Entity e = entity(ctx, entObj);
                Location l = coerceLocation(e);
                if (l != null) return l;
            }

            Object worldObj = firstObject(map, "world", "world_name");
            World w = world(ctx, worldObj, defaultWorld);
            if (w == null) w = defaultWorld != null ? defaultWorld : (ctx != null && ctx.player() != null ? ctx.player().getWorld() : null);
            if (w == null) return null;

            Double x = doubleVal(ctx, firstObject(map, "x"));
            Double y = doubleVal(ctx, firstObject(map, "y"));
            Double z = doubleVal(ctx, firstObject(map, "z"));
            if (x == null || y == null || z == null) return null;

            Location loc = new Location(w, x, y, z);
            Double yaw = doubleVal(ctx, firstObject(map, "yaw"));
            Double pitch = doubleVal(ctx, firstObject(map, "pitch"));
            if (yaw != null) loc.setYaw(yaw.floatValue());
            if (pitch != null) loc.setPitch(pitch.floatValue());
            return loc;
        }

        return null;
    }

    public static NamedTextColor color(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return color(ctx, firstObject(raw, keys));
    }

    public static NamedTextColor color(ScriptContext ctx, Object value) {
        String s = string(ctx, value);
        if (s == null || s.isBlank()) return null;
        try {
            return NamedTextColor.NAMES.value(s.trim().toLowerCase(Locale.ROOT));
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Material material(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return material(ctx, firstObject(raw, keys));
    }

    public static Material material(ScriptContext ctx, Object value) {
        String s = string(ctx, value);
        if (s == null || s.isBlank()) return null;
        try {
            return Material.valueOf(s.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Sound sound(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return sound(ctx, firstObject(raw, keys));
    }

    public static Sound sound(ScriptContext ctx, Object value) {
        String s = string(ctx, value);
        if (s == null || s.isBlank()) return null;
        try {
            return Sound.valueOf(s.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
            return null;
        }
    }

    public static PotionEffectType potionEffectType(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return potionEffectType(ctx, firstObject(raw, keys));
    }

    public static PotionEffectType potionEffectType(ScriptContext ctx, Object value) {
        String s = string(ctx, value);
        if (s == null || s.isBlank()) return null;
        try {
            return PotionEffectType.getByName(s.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Enchantment enchantment(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return enchantment(ctx, firstObject(raw, keys));
    }

    public static Enchantment enchantment(ScriptContext ctx, Object value) {
        String s = string(ctx, value);
        if (s == null || s.isBlank()) return null;
        String normalized = s.trim();
        int colon = normalized.indexOf(':');
        if (colon >= 0 && colon < normalized.length() - 1) {
            normalized = normalized.substring(colon + 1);
        }
        normalized = normalized.trim().toUpperCase(Locale.ROOT);
        return Enchantment.getByName(normalized);
    }

    public static EntityType entityType(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return entityType(ctx, firstObject(raw, keys));
    }

    public static EntityType entityType(ScriptContext ctx, Object value) {
        String s = string(ctx, value);
        if (s == null || s.isBlank()) return null;
        try {
            return EntityType.valueOf(s.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
            return null;
        }
    }

    public static EquipmentSlot equipmentSlot(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return equipmentSlot(ctx, firstObject(raw, keys));
    }

    public static EquipmentSlot equipmentSlot(ScriptContext ctx, Object value) {
        String s = string(ctx, value);
        if (s == null || s.isBlank()) return null;
        String n = s.trim().toUpperCase(Locale.ROOT);
        if ("HELMET".equals(n)) n = "HEAD";
        if ("CHESTPLATE".equals(n)) n = "CHEST";
        if ("LEGGINGS".equals(n)) n = "LEGS";
        if ("BOOTS".equals(n)) n = "FEET";
        try {
            return EquipmentSlot.valueOf(n);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Attribute attribute(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return attribute(ctx, firstObject(raw, keys));
    }

    public static Attribute attribute(ScriptContext ctx, Object value) {
        String s = string(ctx, value);
        if (s == null || s.isBlank()) return null;
        String key = s.trim().toLowerCase(Locale.ROOT);
        if (!key.contains(":")) key = "minecraft:" + key;
        try {
            return org.bukkit.Registry.ATTRIBUTE.get(NamespacedKey.fromString(key));
        } catch (Exception ignored) {
            return null;
        }
    }

    public static AttributeModifier.Operation attributeOperation(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return attributeOperation(ctx, firstObject(raw, keys));
    }

    public static AttributeModifier.Operation attributeOperation(ScriptContext ctx, Object value) {
        String s = string(ctx, value);
        if (s == null || s.isBlank()) return null;
        try {
            return AttributeModifier.Operation.valueOf(s.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
            return null;
        }
    }

    public static EquipmentSlotGroup equipmentSlotGroup(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return equipmentSlotGroup(ctx, firstObject(raw, keys));
    }

    public static EquipmentSlotGroup equipmentSlotGroup(ScriptContext ctx, Object value) {
        String s = string(ctx, value);
        if (s == null || s.isBlank()) return null;
        String n = s.trim().toUpperCase(Locale.ROOT);

        if ("HAND".equals(n) || "MAIN_HAND".equals(n) || "MAIN".equals(n)) n = "MAINHAND";
        if ("HELMET".equals(n)) n = "HEAD";
        if ("CHESTPLATE".equals(n)) n = "CHEST";
        if ("LEGGINGS".equals(n)) n = "LEGS";
        if ("BOOTS".equals(n)) n = "FEET";

        try {
            return switch (n) {
                case "ANY" -> EquipmentSlotGroup.ANY;
                case "MAINHAND" -> EquipmentSlotGroup.MAINHAND;
                case "OFFHAND" -> EquipmentSlotGroup.OFFHAND;
                case "HEAD" -> EquipmentSlotGroup.HEAD;
                case "CHEST" -> EquipmentSlotGroup.CHEST;
                case "LEGS" -> EquipmentSlotGroup.LEGS;
                case "FEET" -> EquipmentSlotGroup.FEET;
                case "ARMOR" -> EquipmentSlotGroup.ARMOR;
                default -> EquipmentSlotGroup.getByName(n);
            };
        } catch (Exception ignored) {
            return EquipmentSlotGroup.ANY;
        }
    }

    public static GameRule<?> resolveGameRule(Object val) {
        if (val == null) return null;
        return gameRule(null, Map.of("k", val), "k");
    }

    public static GameRule<?> gameRule(ScriptContext ctx, Map<?, ?> raw, String... keys) {
        return gameRule(ctx, firstObject(raw, keys));
    }

    public static GameRule<?> gameRule(ScriptContext ctx, Object value) {
        String s = string(ctx, value);
        if (s == null || s.isBlank()) return null;
        return GameRule.getByName(s.trim());
    }

    public static Location resolveLocation(ScriptContext ctx, Object val) {
        return location(ctx, val, ctx != null && ctx.player() != null ? ctx.player().getWorld() : null);
    }

    public static NamedTextColor resolveColor(Object val) {
        return color(null, val);
    }

    public static Material resolveMaterial(Object val) {
        return material(null, val);
    }

    public static EquipmentSlot resolveEquipmentSlot(Object val) {
        return equipmentSlot(null, val);
    }

    public static EquipmentSlotGroup resolveEquipmentSlotGroup(Object val) {
        return equipmentSlotGroup(null, val);
    }

    public static Double resolveDouble(Object val) {
        return doubleVal(null, val);
    }

    public static EntityType resolveEntityType(Object val) {
        return entityType(null, val);
    }

    public static World world(ScriptContext ctx, Object spec) {
        return world(ctx, spec, ctx != null && ctx.player() != null ? ctx.player().getWorld() : null);
    }

    public static World world(ScriptContext ctx, Object spec, World defaultWorld) {
        if (spec == null) return defaultWorld;

        if (spec instanceof World w) return w;
        if (spec instanceof Location l) return l.getWorld() != null ? l.getWorld() : defaultWorld;
        if (spec instanceof Entity e) {
            try {
                World w = e.getWorld();
                return w != null ? w : defaultWorld;
            } catch (Exception ignored) {
                return defaultWorld;
            }
        }

        Object resolved = resolve(ctx, spec);
        if (resolved != null && resolved != spec) {
            return world(ctx, resolved, defaultWorld);
        }

        if (spec instanceof String s) {
            String t = s.trim();
            if (t.isEmpty()) return defaultWorld;

            Object v = ctx != null ? ctx.getValue(t) : null;
            if (v != null && v != spec) return world(ctx, v, defaultWorld);

            try {
                World byName = Bukkit.getWorld(t);
                return byName != null ? byName : defaultWorld;
            } catch (Exception ignored) {
                return defaultWorld;
            }
        }

        if (spec instanceof Map<?, ?> map) {
            Object key = firstObject(map, "key", "var", "ref");
            if (key != null) return world(ctx, key, defaultWorld);

            Object name = firstObject(map, "world", "world_name", "name");
            if (name != null) return world(ctx, name, defaultWorld);
        }

        return defaultWorld;
    }

    public static List<Entity> entities(ScriptContext ctx, Object spec) {
        if (spec == null) return List.of();

        Object resolved = resolve(ctx, spec);
        if (resolved != null && resolved != spec) {
            return entities(ctx, resolved);
        }

        if (spec instanceof List<?> list) {
            return list.stream()
                .map(o -> entities(ctx, o))
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .toList();
        }

        if (spec instanceof Entity e) return List.of(e);
        
        if (spec instanceof String s) {
            String t = s.trim();
            if (t.isEmpty()) return List.of();

            // Simple selectors
            if (t.equalsIgnoreCase("@a") || t.equalsIgnoreCase("all_players")) {
                return new java.util.ArrayList<>(Bukkit.getOnlinePlayers());
            }
            if (t.equalsIgnoreCase("@r") || t.equalsIgnoreCase("random_player")) {
                var players = new java.util.ArrayList<>(Bukkit.getOnlinePlayers());
                if (players.isEmpty()) return List.of();
                return List.of(players.get(java.util.concurrent.ThreadLocalRandom.current().nextInt(players.size())));
            }
            if (t.equalsIgnoreCase("@s") || t.equalsIgnoreCase("self") || t.equalsIgnoreCase("subject")) {
                Entity e = ctx != null ? ctx.subject() : null;
                return e != null ? List.of(e) : List.of();
            }
            if (t.equalsIgnoreCase("target")) {
                Object v = ctx != null ? ctx.getValue("TARGET") : null;
                return entities(ctx, v);
            }

            // Fallback to single entity resolution
            Entity e = entity(ctx, t);
            return e != null ? List.of(e) : List.of();
        }

        if (spec instanceof Map<?, ?> map) {
            if (map.containsKey("source") || map.containsKey("selector")) {
                return selectEntities(ctx, map);
            }
        }

        Entity e = entity(ctx, spec);
        return e != null ? List.of(e) : List.of();
    }

    public static List<Entity> selectEntities(ScriptContext ctx, Map<?, ?> raw) {
        return selectEntities(ctx, raw, null);
    }

    public static List<Entity> selectEntities(ScriptContext ctx, Map<?, ?> raw, List<Condition> conditions) {
        List<Entity> candidates = new ArrayList<>();
        String source = string(ctx, raw, "source", "selector");
        Object locationSpec = raw.get("location");
        Double radius = doubleVal(ctx, raw, "radius", "r");
        
        Object typeSpec = raw.get("type");
        String tag = string(ctx, raw, "tag");
        String name = string(ctx, raw, "name");
        
        Integer limit = integer(ctx, raw, "limit", "count");
        String sort = string(ctx, raw, "sort");
        
        boolean includePlayers = bool(ctx, raw.get("include_players")) != Boolean.FALSE; // default true
        boolean includeMobs = bool(ctx, raw.get("include_mobs")) != Boolean.FALSE; // default true
        
        Location center = location(ctx, locationSpec);
        if (center == null && ctx != null && ctx.location() != null) center = ctx.location();
        
        String src = source != null ? source.toLowerCase(Locale.ROOT) : "world";
        
        if ("server".equals(src) || "global".equals(src)) {
            for (World w : Bukkit.getWorlds()) {
                candidates.addAll(w.getEntities());
            }
        } else if ("nearby".equals(src) && center != null && radius != null) {
            if (center.getWorld() != null) {
                candidates.addAll(center.getWorld().getNearbyEntities(center, radius, radius, radius));
            }
        } else if ("world".equals(src)) {
            World w = center != null ? center.getWorld() : (ctx != null && ctx.player() != null ? ctx.player().getWorld() : null);
            if (w != null) {
                candidates.addAll(w.getEntities());
            }
        } else {
            // Try to resolve source as a variable (list of entities or single entity)
            Object val = ctx != null ? ctx.getValue(source) : null;
            if (val instanceof List<?> list) {
                for (Object o : list) {
                    if (o instanceof Entity e) candidates.add(e);
                }
            } else if (val instanceof Entity e) {
                candidates.add(e);
            } else {
                // Fallback to world if not found? Or empty?
                // Original logic fell back to world if not server/nearby.
                // But now we check "world" explicitly.
                // If source is provided but not found, we should probably return empty or fallback to world if source was null.
                // But source is not null here (we checked src).
                // If the user provided a source string that isn't a keyword and isn't a var, maybe it's a selector?
                // But we don't support vanilla selectors here fully yet (except via entities() method).
                
                // Let's try Resolvers.entities(ctx, source) as a fallback?
                // But that might cause recursion if it calls selectEntities.
                // Resolvers.entities calls selectEntities if map has source/selector.
                // Here source is a string. Resolvers.entities(string) handles @a, @p, etc.
                
                List<Entity> resolved = entities(ctx, source);
                if (!resolved.isEmpty()) {
                    candidates.addAll(resolved);
                } else {
                    // Default behavior for unknown source: world of center/player
                    World w = center != null ? center.getWorld() : (ctx != null && ctx.player() != null ? ctx.player().getWorld() : null);
                    if (w != null) {
                        candidates.addAll(w.getEntities());
                    }
                }
            }
        }

        // Filter
        List<Entity> filtered = candidates.stream().filter(e -> {
            if (!includePlayers && e instanceof Player) return false;
            if (!includeMobs && e instanceof LivingEntity && !(e instanceof Player)) return false; // simplified mob check

            if (typeSpec != null) {
                EntityType et = resolveEntityType(typeSpec);
                if (et != null && e.getType() != et) return false;
            }
            if (tag != null && !e.getScoreboardTags().contains(tag)) return false;
            if (name != null && !name.equals(e.getName()) && !name.equals(e.getCustomName())) return false;
            
            if (conditions != null && !conditions.isEmpty() && ctx != null) {
                Map<String, Object> vars = ctx.variables() != null ? new HashMap<>(ctx.variables()) : new HashMap<>();
                vars.put("SUBJECT", e);
                vars.put("entity", e);
                ScriptContext checkCtx = new ScriptContext(ctx.plugin(), ctx.player(), e.getUniqueId().toString(), ctx.phase(), vars);
                for (Condition c : conditions) {
                    if (!c.test(checkCtx)) return false;
                }
            }
            
            return true;
        }).collect(Collectors.toList());

        // Sort
        if (sort != null && center != null) {
            final Location finalCenter = center;
            switch (sort.toLowerCase(Locale.ROOT)) {
                case "nearest", "closest" -> filtered.sort(Comparator.comparingDouble(e -> e.getLocation().distanceSquared(finalCenter)));
                case "furthest", "farthest" -> filtered.sort((a, b) -> Double.compare(b.getLocation().distanceSquared(finalCenter), a.getLocation().distanceSquared(finalCenter)));
                case "random" -> Collections.shuffle(filtered);
            }
        } else if ("random".equalsIgnoreCase(sort)) {
            Collections.shuffle(filtered);
        }

        // Limit
        if (limit != null && limit > 0 && filtered.size() > limit) {
            filtered = filtered.subList(0, limit);
        }
        
        return filtered;
    }

    public static List<Location> locations(ScriptContext ctx, Object spec) {
        if (spec == null) return List.of();

        Object resolved = resolve(ctx, spec);
        if (resolved != null && resolved != spec) {
            return locations(ctx, resolved);
        }

        if (spec instanceof List<?> list) {
            return list.stream()
                .map(o -> locations(ctx, o))
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .toList();
        }

        Location l = location(ctx, spec);
        if (l != null) return List.of(l);

        // Try resolving as entities and getting their locations
        List<Entity> ents = entities(ctx, spec);
        if (!ents.isEmpty()) {
            return ents.stream()
                .map(Resolvers::coerceLocation)
                .filter(Objects::nonNull)
                .toList();
        }

        return List.of();
    }

    public static Entity entity(ScriptContext ctx, Object spec) {
        if (spec == null) return null;

        if (spec instanceof Entity e) return e;
        if (spec instanceof UUID id) {
            try {
                return Bukkit.getEntity(id);
            } catch (Exception ignored) {
                return null;
            }
        }

        Object resolved = resolve(ctx, spec);
        if (resolved != null && resolved != spec) {
            return entity(ctx, resolved);
        }

        if (spec instanceof String s) {
            String t = s.trim();
            if (t.isEmpty()) return null;

            Object v = ctx != null ? ctx.getValue(t) : null;
            Entity fromCtx = entity(ctx, v);
            if (fromCtx != null) return fromCtx;

            try {
                UUID parsed = UUID.fromString(t);
                return Bukkit.getEntity(parsed);
            } catch (Exception ignored) {
                // fallthrough
            }

            try {
                Player p = Bukkit.getPlayerExact(t);
                if (p != null) return p;
            } catch (Exception ignored) {
                // fallthrough
            }

            return null;
        }

        if (spec instanceof Map<?, ?> map) {
            Object key = firstObject(map, "key", "var", "ref");
            if (key != null) return entity(ctx, key);

            Object uuid = firstObject(map, "uuid", "id");
            if (uuid != null) return entity(ctx, uuid);

            Object name = firstObject(map, "name", "player");
            if (name != null) return entity(ctx, name);
        }

        return null;
    }

    public static Map<Enchantment, Integer> resolveEnchantmentMap(ScriptContext ctx, Object rawObj) {
        if (!(rawObj instanceof java.util.List<?> list) || list.isEmpty()) return java.util.Map.of();
        java.util.Map<Enchantment, Integer> out = new java.util.HashMap<>();

        for (Object o : list) {
            if (!(o instanceof Map<?, ?> m)) continue;

            String name = string(ctx, m, "enchant", "enchantment");
            if (name == null || name.isBlank()) continue;

            Enchantment ench = enchantment(ctx, name);
            if (ench == null) continue;

            Integer lvlObj = integer(ctx, m, "level");
            int lvl = lvlObj != null ? Math.max(1, lvlObj) : 1;

            Integer prev = out.get(ench);
            if (prev == null || lvl > prev) {
                out.put(ench, lvl);
            }
        }
        return out.isEmpty() ? java.util.Map.of() : out;
    }

    private static Object firstObject(Map<?, ?> raw, String... keys) {
        if (raw == null || keys == null) return null;
        for (String k : keys) {
            if (k == null || k.isBlank()) continue;
            Object v = raw.get(k);
            if (v != null) return v;
        }
        return null;
    }

    /**
     * Parses a list of action specs into executable actions using the BuiltInActions parser.
     */
    public static List<Action> parseActionList(Object actionsObj) {
        if (!(actionsObj instanceof List<?> list) || list.isEmpty()) return null;
        List<Action> actions = list.stream()
            .filter(Map.class::isInstance)
            .map(m -> ActionRegistrar.parse((Map<?, ?>) m))
            .filter(a -> a != null)
            .toList();
        return actions.isEmpty() ? null : actions;
    }

    /**
     * Interpolate strings containing ${key} placeholders. If the entire string is a single
     * placeholder and the resolved value is a Number/Boolean/String, we return that value
     * directly so numeric parsers can consume it. For mixed text we always return a String.
     * Unsupported object types inside interpolation return null to signal fallback to the
     * original value (caller keeps the raw string), matching the "solo retornar el valor de la primera key"
     * intent.
     */
    private static Object interpolate(ScriptContext ctx, String input) {
        if (input == null || input.isEmpty()) return null;
        int start = input.indexOf("${");
        if (start < 0) return null;

        // Fast path: single placeholder covering the whole string
        if (start == 0 && input.endsWith("}")) {
            String token = input.substring(2, input.length() - 1);
            return resolveToken(ctx, token, true);
        }

        StringBuilder sb = new StringBuilder();
        int idx = 0;
        while (true) {
            int open = input.indexOf("${", idx);
            if (open < 0) {
                sb.append(input.substring(idx));
                break;
            }
            sb.append(input, idx, open);
            int close = input.indexOf('}', open + 2);
            if (close < 0) {
                // malformed, give up
                return null;
            }
            String token = input.substring(open + 2, close);
            Object resolved = resolveToken(ctx, token, false);
            if (resolved == null) return null;
            sb.append(String.valueOf(resolved));
            idx = close + 1;
        }
        return sb.toString();
    }

    private static Object resolveToken(ScriptContext ctx, String token, boolean allowNonStringReturn) {
        if (token == null) return null;
        String raw = token.trim();
        if (raw.isEmpty()) return null;

        // Modifiers pipeline: base|modifier[:arg[:arg2...]]
        String[] parts = raw.split("\\|", -1);
        String baseKey = parts[0].trim();
        Object val = ctx != null ? ctx.getValue(baseKey) : null;
        if (val == null) val = baseKey; // allow literal fallback when ctx missing

        for (int i = 1; i < parts.length; i++) {
            String mod = parts[i].trim();
            if (mod.isEmpty()) continue;
            val = applyModifier(val, mod);
        }

        if (val == null) return null;

        // Allowed direct returns (for numeric parsing) only if the whole input was the placeholder
        if (allowNonStringReturn && (val instanceof Number || val instanceof Boolean || val instanceof String)) {
            return val;
        }

        // For mixed interpolation, only Strings/numbers/booleans make sense; otherwise fail
        if (val instanceof Number || val instanceof Boolean) return String.valueOf(val);
        if (val instanceof String) return val;

        return null;
    }

    private static Object applyModifier(Object val, String mod) {
        // format: replace:from:to | strip | lower | upper | split:sep | index:n
        if (val == null) return null;

        String[] segs = mod.split(":", -1);
        String name = segs[0].trim().toLowerCase(Locale.ROOT);

        switch (name) {
            case "strip" -> {
                return (val instanceof String s) ? s.trim() : val;
            }
            case "lower" -> {
                return (val instanceof String s) ? s.toLowerCase(Locale.ROOT) : val;
            }
            case "upper" -> {
                return (val instanceof String s) ? s.toUpperCase(Locale.ROOT) : val;
            }
            case "replace" -> {
                if (!(val instanceof String s)) return val;
                String from = segs.length > 1 ? segs[1] : "";
                String to = segs.length > 2 ? segs[2] : "";
                return s.replace(from, to);
            }
            case "split" -> {
                if (!(val instanceof String s)) return val;
                String sep = segs.length > 1 ? segs[1] : ",";
                return java.util.Arrays.asList(s.split(java.util.regex.Pattern.quote(sep)));
            }
            case "index" -> {
                if (segs.length < 2) return val;
                int idx;
                try {
                    idx = Integer.parseInt(segs[1]);
                } catch (Exception e) {
                    return val;
                }
                if (val instanceof java.util.List<?> list) {
                    return (idx >= 0 && idx < list.size()) ? list.get(idx) : null;
                }
                if (val.getClass().isArray()) {
                    int len = java.lang.reflect.Array.getLength(val);
                    return (idx >= 0 && idx < len) ? java.lang.reflect.Array.get(val, idx) : null;
                }
                return val;
            }
            default -> {
                return val;
            }
        }
    }

    private static Location coerceLocation(Object v) {
        if (v instanceof Location l) return l;
        if (v instanceof Entity e) {
            try {
                return e.getLocation();
            } catch (Exception ignored) {
                return null;
            }
        }
        if (v instanceof Block b) {
            try {
                return b.getLocation();
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }

    public static Object resolveProperty(Object target, String property) {
        if (target == null || property == null) return null;

        if (property.indexOf('.') > 0) {
            String[] parts = property.split("\\.", 2);
            Object first = resolveProperty(target, parts[0]);
            return resolveProperty(first, parts[1]);
        }

        String prop = property.toLowerCase(Locale.ROOT);

        if (target instanceof Entity e) {
            switch (prop) {
                case "uuid": return e.getUniqueId().toString();
                case "name": return e.getName();
                case "custom_name": return e.getCustomName();
                case "type": return e.getType().name();
                case "location": return e.getLocation();
                case "world": return e.getWorld();
                case "velocity": return e.getVelocity();
                case "fall_distance": return e.getFallDistance();
                case "fire_ticks": return e.getFireTicks();
                case "freeze_ticks": return e.getFreezeTicks();
                case "height": return e.getHeight();
                case "width": return e.getWidth();
                case "is_dead": return e.isDead();
                case "is_valid": return e.isValid();
                case "is_on_ground": return e.isOnGround();
                case "is_glowing": return e.isGlowing();
                case "is_invulnerable": return e.isInvulnerable();
                case "is_silent": return e.isSilent();
                case "is_custom_name_visible": return e.isCustomNameVisible();
                case "passengers": return e.getPassengers();
                case "vehicle": return e.getVehicle();
                case "facing": return e.getFacing().name();
                case "x": return e.getLocation().getX();
                case "y": return e.getLocation().getY();
                case "z": return e.getLocation().getZ();
                case "yaw": return e.getLocation().getYaw();
                case "pitch": return e.getLocation().getPitch();
                case "is_monster": return e instanceof org.bukkit.entity.Monster;
            }
            if (e instanceof LivingEntity le) {
                switch (prop) {
                    case "health": return le.getHealth();
                    case "max_health": {
                        var attr = le.getAttribute(Attribute.MAX_HEALTH);
                        return attr != null ? attr.getValue() : null;
                    }
                    case "absorption": return le.getAbsorptionAmount();
                    case "air": return le.getRemainingAir();
                    case "max_air": return le.getMaximumAir();
                    case "eye_location": return le.getEyeLocation();
                    case "eye_height": return le.getEyeHeight();
                    case "is_gliding": return le.isGliding();
                    case "is_swimming": return le.isSwimming();
                    case "is_climbing": return le.isClimbing();
                    case "is_invisible": return le.isInvisible();
                    case "is_leashed": return le.isLeashed();
                }
            }
            if (e instanceof Player p) {
                switch (prop) {
                    case "display_name": return p.getDisplayName(); // Legacy text
                    case "gamemode": return p.getGameMode().name();
                    case "ping": return p.getPing();
                    case "locale": return p.getLocale();
                    case "address": return p.getAddress() != null ? p.getAddress().toString() : null;
                    case "exp": return p.getExp();
                    case "level": return p.getLevel();
                    case "total_exp": return p.getTotalExperience();
                    case "fly_speed": return p.getFlySpeed();
                    case "walk_speed": return p.getWalkSpeed();
                    case "is_flying": return p.isFlying();
                    case "is_sneaking": return p.isSneaking();
                    case "is_sprinting": return p.isSprinting();
                    case "is_blocking": return p.isBlocking();
                    case "is_sleeping": return p.isSleeping();
                    case "is_op": return p.isOp();
                    case "food": return p.getFoodLevel();
                    case "saturation": return p.getSaturation();
                }
            }
        }

        if (target instanceof Block b) {
            switch (prop) {
                case "type": return b.getType().name();
                case "material": return b.getType().name();
                case "data": return b.getBlockData().getAsString();
                case "location": return b.getLocation();
                case "world": return b.getWorld();
                case "x": return b.getX();
                case "y": return b.getY();
                case "z": return b.getZ();
                case "light": return b.getLightLevel();
                case "block_power": return b.getBlockPower();
                case "temperature": return b.getTemperature();
                case "humidity": return b.getHumidity();
                case "biome": return b.getBiome().name();
                case "is_passable": return b.isPassable();
                case "is_liquid": return b.isLiquid();
                case "is_empty": return b.isEmpty();
            }
        }

        if (target instanceof Location l) {
            switch (prop) {
                case "world": return l.getWorld();
                case "x": return l.getX();
                case "y": return l.getY();
                case "z": return l.getZ();
                case "yaw": return l.getYaw();
                case "pitch": return l.getPitch();
                case "block_x": return l.getBlockX();
                case "block_y": return l.getBlockY();
                case "block_z": return l.getBlockZ();
                case "chunk": return l.getChunk();
                case "block": return l.getBlock();
                case "block_below": return l.getBlock().getRelative(0, -1, 0);
            }
        }

        if (target instanceof World w) {
            switch (prop) {
                case "name": return w.getName();
                case "uuid": return w.getUID().toString();
                case "time": return w.getTime();
                case "full_time": return w.getFullTime();
                case "weather": return w.isThundering() ? "thunder" : (w.hasStorm() ? "rain" : "clear");
                case "storm": return w.hasStorm();
                case "thundering": return w.isThundering();
                case "seed": return w.getSeed();
                case "environment": return w.getEnvironment().name();
                case "difficulty": return w.getDifficulty().name();
                case "max_height": return w.getMaxHeight();
                case "min_height": return w.getMinHeight();
                case "sea_level": return w.getSeaLevel();
                case "player_count": return w.getPlayerCount();
            }
        }

        if (target instanceof org.bukkit.util.Vector v) {
            switch (prop) {
                case "x": return v.getX();
                case "y": return v.getY();
                case "z": return v.getZ();
                case "length": return v.length();
                case "length_squared": return v.lengthSquared();
            }
        }

        if (target instanceof ItemStack is) {
            switch (prop) {
                case "type": return is.getType().name();
                case "amount": return is.getAmount();
                case "max_stack_size": return is.getMaxStackSize();
                case "is_air": return is.getType().isAir();
                case "is_edible": return is.getType().isEdible();
                case "has_item_meta": return is.hasItemMeta();
                case "display_name": return is.hasItemMeta() && is.getItemMeta().hasDisplayName() ? is.getItemMeta().getDisplayName() : null;
                case "custom_model_data": return is.hasItemMeta() && is.getItemMeta().hasCustomModelData() ? is.getItemMeta().getCustomModelData() : null;
                case "lore": return is.hasItemMeta() && is.getItemMeta().hasLore() ? is.getItemMeta().getLore() : null;
                case "pdc": {
                    if (!is.hasItemMeta()) return null;
                    PersistentDataContainer pdc = is.getItemMeta().getPersistentDataContainer();
                    Map<String, Object> out = new HashMap<>();
                    for (NamespacedKey nk : pdc.getKeys()) {
                        String full = nk.toString();
                        Object v = null;
                        try { v = pdc.get(nk, PersistentDataType.STRING); } catch (Exception e) {}
                        if (v == null) try { v = pdc.get(nk, PersistentDataType.INTEGER); } catch (Exception e) {}
                        if (v == null) try { v = pdc.get(nk, PersistentDataType.LONG); } catch (Exception e) {}
                        if (v == null) try { v = pdc.get(nk, PersistentDataType.DOUBLE); } catch (Exception e) {}
                        if (v == null) try { v = pdc.get(nk, PersistentDataType.BYTE); } catch (Exception e) {}
                        if (v != null) out.put(full, v);
                    }
                    return out.isEmpty() ? null : out;
                }
            }
        }

        if (target instanceof Map<?, ?> map) {
            return map.get(property); // Case sensitive for maps? Or try lowercase? Let's stick to exact match or lowercase if not found.
        }

        return null;
    }
}
