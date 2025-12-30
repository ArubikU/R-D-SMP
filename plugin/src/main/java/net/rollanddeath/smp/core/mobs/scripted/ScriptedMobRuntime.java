package net.rollanddeath.smp.core.mobs.scripted;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.scripted.ModifierRule;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import net.rollanddeath.smp.core.scripting.ScriptPhase;
import net.rollanddeath.smp.core.scripting.ScriptVars;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.scheduler.BukkitTask;

import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ScriptedMobRuntime implements Listener {

    private final RollAndDeathSMP plugin;

    private final Map<String, ScriptedMobDefinition> definitions = new HashMap<>();
    private final Map<String, Set<UUID>> tracked = new HashMap<>();

    private BukkitTask tickTask;

    public ScriptedMobRuntime(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    public void setDefinitions(Map<String, ScriptedMobDefinition> defs) {
        definitions.clear();
        tracked.clear();
        if (defs != null) {
            definitions.putAll(defs);
            for (String t : defs.keySet()) {
                tracked.put(t, new HashSet<>());
            }
        }
    }

    public boolean hasDefinition(String type) {
        return definitions.containsKey(type);
    }

    public ScriptedMobDefinition getDefinition(String type) {
        return type != null ? definitions.get(type) : null;
    }

    public void start() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        boolean needsTick = definitions.values().stream().anyMatch(d -> d.events() != null && d.events().containsKey("mob_tick"));
        if (needsTick) {
            tickTask = Bukkit.getScheduler().runTaskTimer(plugin, this::onTick, 20L, 20L);
        }
    }

    public void stop() {
        HandlerList.unregisterAll(this);
        if (tickTask != null && !tickTask.isCancelled()) {
            tickTask.cancel();
        }
        tracked.clear();
        definitions.clear();
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // Los hijos de Slime/MagmaCube heredan tags del padre.
        // No queremos ejecutar `mob_spawn` aquí: para SLIME_SPLIT usamos el hook dedicado
        // `slime_split_child` (disparado desde MobSpawnListener) y luego se limpian tags.
        // Ejecutar `mob_spawn` en los hijos puede re-aplicar tamaño/atributos de boss y causar duplicación infinita.
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) {
            return;
        }

        LivingEntity entity = event.getEntity();
        String type = resolveMobType(entity);
        if (type == null) return;

        ScriptedMobDefinition def = definitions.get(type);
        if (def == null) return;

        Set<UUID> ids = tracked.get(type);
        if (ids != null) {
            ids.add(entity.getUniqueId());
        }

        ModifierRule rule = def.events() != null ? def.events().get("mob_spawn") : null;
        if (rule == null) return;

        Map<String, Object> vars = baseVars(entity)
            .event(event)
            .build();

        applyRule(def, rule, null, "mob_spawn", vars);
    }

    /**
     * Ejecuta el evento scripted `mob_spawn` manualmente.
     * Útil para mobs spawneados por el plugin, porque las scoreboard tags se añaden
     * después de que Bukkit dispare el {@link CreatureSpawnEvent}.
     */
    public void onScriptedMobSpawn(LivingEntity entity, CreatureSpawnEvent.SpawnReason reason) {
        if (entity == null) return;
        if (reason == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) {
            // Para SLIME_SPLIT se usa slime_split_child, no mob_spawn.
            return;
        }

        String type = resolveMobType(entity);
        if (type == null) return;

        ScriptedMobDefinition def = definitions.get(type);
        if (def == null) return;

        Set<UUID> ids = tracked.get(type);
        if (ids != null) {
            ids.add(entity.getUniqueId());
        }

        ModifierRule rule = def.events() != null ? def.events().get("mob_spawn") : null;
        if (rule == null) return;

        // No hay Bukkit Event aquí: exponemos un EVENT mínimo como Map.
        Map<String, Object> ev = new java.util.HashMap<>();
        ev.put("type", "mob_spawn");
        ev.put("spawnReason", reason != null ? reason.name() : null);

        Map<String, Object> vars = baseVars(entity)
            .event(ev)
            .build();

        applyRule(def, rule, null, "mob_spawn", vars);
    }

    @EventHandler
    public void onMobDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        String type = resolveMobType(entity);
        if (type == null) return;

        ScriptedMobDefinition def = definitions.get(type);
        if (def == null) return;

        ModifierRule rule = def.events() != null ? def.events().get("mob_damage") : null;
        if (rule == null) return;

        Map<String, Object> vars = baseVars(entity)
            .event(event)
            .build();

        boolean deny = applyRule(def, rule, null, "mob_damage", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMobDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        String type = resolveMobType(entity);
        if (type == null) return;

        ScriptedMobDefinition def = definitions.get(type);
        if (def == null) return;

        ModifierRule rule = def.events() != null ? def.events().get("mob_damage_by_entity") : null;
        if (rule == null) return;

        Player player = event.getDamager() instanceof Player p ? p : null;

        ScriptVars varsBuilder = baseVars(entity).event(event);
        try {
            var damager = event.getDamager();
            if (damager instanceof org.bukkit.entity.Projectile pr) {
                varsBuilder.projectile(pr);
                Object shooter = null;
                try {
                    shooter = pr.getShooter();
                } catch (Exception ignored) {
                }
                if (shooter instanceof org.bukkit.entity.Entity se) {
                    varsBuilder.target(se);
                }
            } else {
                varsBuilder.target(damager);
            }
        } catch (Exception ignored) {
        }

        Map<String, Object> vars = varsBuilder.build();

        boolean deny = applyRule(def, rule, player, "mob_damage_by_entity", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        String type = resolveMobType(entity);
        if (type == null) return;

        ScriptedMobDefinition def = definitions.get(type);
        if (def == null) return;

        Set<UUID> ids = tracked.get(type);
        if (ids != null) {
            ids.remove(entity.getUniqueId());
        }

        ModifierRule rule = def.events() != null ? def.events().get("mob_death") : null;

        Player killer = null;
        try {
            killer = entity.getKiller();
        } catch (Exception ignored) {
        }

        ScriptVars varsBuilder = baseVars(entity).event(event);
        if (killer != null) {
            varsBuilder.target(killer);
        }
        Map<String, Object> vars = varsBuilder.build();

        if (rule != null) {
            applyRule(def, rule, killer, "mob_death", vars);
        }

        // Loot sugar: añade drops definidos en mobs.yml
        if (def.loot() != null && !def.loot().isEmpty()) {
            for (var le : def.loot()) {
                if (le == null) continue;
                double p = Math.max(0.0, Math.min(1.0, le.chance()));
                if (p < 1.0 && ThreadLocalRandom.current().nextDouble() >= p) continue;

                // custom_item (String ID)
                String custom = le.customItem();
                if (custom != null && !custom.isBlank()) {
                    try {
                        var item = plugin.getItemManager() != null ? plugin.getItemManager().getItem(custom) : null;
                        if (item != null) {
                            event.getDrops().add(item.getItemStack());
                            continue;
                        }
                    } catch (Exception ignored) {
                    }
                }

                // material
                String matRaw = le.material();
                if (matRaw == null || matRaw.isBlank()) continue;

                Material mat;
                try {
                    mat = Material.valueOf(matRaw.trim().toUpperCase(Locale.ROOT));
                } catch (Exception e) {
                    mat = null;
                }
                if (mat == null || mat == Material.AIR) continue;
                event.getDrops().add(new ItemStack(mat, Math.max(1, le.amount())));
            }
        }
    }

    @EventHandler
    public void onMobExplode(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        String type = resolveMobType(entity);
        if (type == null) return;

        ScriptedMobDefinition def = definitions.get(type);
        if (def == null) return;

        ModifierRule rule = def.events() != null ? def.events().get("mob_explode") : null;
        if (rule == null) return;

        Map<String, Object> vars = baseVars(entity)
            .event(event)
            .build();

        boolean deny = applyRule(def, rule, null, "mob_explode", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMobTeleport(EntityTeleportEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        String type = resolveMobType(entity);
        if (type == null) return;

        ScriptedMobDefinition def = definitions.get(type);
        if (def == null) return;

        ModifierRule rule = def.events() != null ? def.events().get("mob_teleport") : null;
        if (rule == null) return;

        Map<String, Object> vars = baseVars(entity)
            .event(event)
            .build();

        boolean deny = applyRule(def, rule, null, "mob_teleport", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    public void onSlimeSplitChild(LivingEntity child) {
        String type = resolveMobType(child);
        if (type == null) return;

        ScriptedMobDefinition def = definitions.get(type);
        if (def == null) return;

        ModifierRule rule = def.events() != null ? def.events().get("slime_split_child") : null;
        if (rule == null) return;

        Map<String, Object> vars = baseVars(child).build();

        applyRule(def, rule, null, "slime_split_child", vars);
    }

    private void onTick() {
        for (var entry : tracked.entrySet()) {
            String type = entry.getKey();
            ScriptedMobDefinition def = definitions.get(type);
            if (def == null) continue;

            ModifierRule rule = def.events() != null ? def.events().get("mob_tick") : null;
            if (rule == null) continue;

            Set<UUID> ids = entry.getValue();
            if (ids == null || ids.isEmpty()) continue;

            ids.removeIf(id -> {
                var e = Bukkit.getEntity(id);
                if (!(e instanceof LivingEntity le) || le.isDead()) return true;

                Map<String, Object> vars = baseVars(le).build();

                applyRule(def, rule, null, "mob_tick", vars);
                return false;
            });
        }
    }

    private String resolveMobType(LivingEntity entity) {
        if (entity == null) return null;

        Set<String> tags;
        try {
            tags = entity.getScoreboardTags();
        } catch (Exception e) {
            return null;
        }
        if (tags == null || !tags.contains("custom_mob")) return null;

        for (String t : definitions.keySet()) {
            if (tags.contains(t)) return t;
        }
        return null;
    }

    private ScriptVars baseVars(LivingEntity entity) {
        // 100% scopes: el mob es SUBJECT, y EVENT expone el Bukkit Event reflectivo cuando exista.
        return ScriptVars.create().subject(entity);
    }

    private boolean applyRule(ScriptedMobDefinition def, ModifierRule rule, Player player, String subject, Map<String, Object> vars) {
        String subjectId = "mob:" + def.id() + ":" + subject.toLowerCase(Locale.ROOT);
        ScriptContext ctx = new ScriptContext(plugin, player, subjectId, ScriptPhase.MOB, vars);

        boolean pass = ScriptEngine.allConditionsPass(ctx, rule.requireAll());
        if (pass) {
            ActionResult r = ScriptEngine.runAllWithResult(ctx, rule.onPass());
            return r != null && r.deny();
        }

        ActionResult r = ScriptEngine.runAllWithResult(ctx, rule.onFail());
        return rule.denyOnFail() || (r != null && r.deny());
    }
}
