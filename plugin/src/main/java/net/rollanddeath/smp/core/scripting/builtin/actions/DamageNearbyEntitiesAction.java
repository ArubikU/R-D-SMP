package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/** Damages entities in an area with optional type filters and caster exclusions. */
public final class DamageNearbyEntitiesAction {
    private DamageNearbyEntitiesAction() {
    }

    static void register() {
        ActionRegistrar.register("damage_nearby_entities", DamageNearbyEntitiesAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Double radius = Resolvers.doubleVal(null, raw, "radius");
        Double amount = Resolvers.doubleVal(null, raw, "amount");
        Double multiplier = Resolvers.doubleVal(null, raw, "multiplier");
        if (radius == null || radius <= 0) return null;
        if (amount == null && multiplier == null) return null;

        Object centerSpec = firstNonNull(raw, "center", "location", "at");
        Object casterSpec = firstNonNull(raw, "subject", "caster", "source", "damager");
        Object entityTypeObj = firstNonNull(raw, "entity_type", "type");
        Object entityTypesObj = firstNonNull(raw, "entity_types", "types");
        Object excludeTypesObj = firstNonNull(raw, "exclude_entity_types", "exclude_types");

        boolean excludeCaster = raw.get("exclude_subject") instanceof Boolean b ? b
            : (raw.get("exclude_caster") instanceof Boolean b2 ? b2 : true);
        if (raw.get("exclude_player") instanceof Boolean legacy) {
            excludeCaster = legacy;
        }
        boolean excludeEventTarget = raw.get("exclude_target") instanceof Boolean b ? b
            : (raw.get("exclude_event_target") instanceof Boolean b2 ? b2 : true);

        double r = radius;
        return ctx -> execute(ctx, centerSpec, casterSpec, entityTypeObj, entityTypesObj, excludeTypesObj, r, amount, multiplier, excludeCaster, excludeEventTarget);
    }

    private static ActionResult execute(
        ScriptContext ctx,
        Object centerSpec,
        Object casterSpec,
        Object entityTypeObj,
        Object entityTypesObj,
        Object excludeTypesObj,
        double radius,
        Double amount,
        Double multiplier,
        boolean excludeCaster,
        boolean excludeEventTarget
    ) {
        RollAndDeathSMP plugin = ctx.plugin();
        if (plugin == null) return ActionResult.ALLOW;

        Entity caster = casterSpec != null ? Resolvers.entity(casterSpec, ctx) : null;
        if (caster == null) {
            Object subject = ctx.subject();
            if (subject != null) {
                caster = Resolvers.entity(subject, ctx);
            }
        }
        if (caster == null && ctx.player() != null) {
            caster = ctx.player();
        }

        Location center = null;
        World defaultWorld = caster != null && caster.getWorld() != null ? caster.getWorld()
            : (ctx.player() != null ? ctx.player().getWorld() : null);

        if (centerSpec != null) {
            center = Resolvers.location(centerSpec, ctx, defaultWorld);
        }
        if (center == null) {
            if (caster != null) {
                center = caster.getLocation();
            } else {
                EntityDamageEvent entityEvent = ctx.nativeEvent(EntityDamageEvent.class);
                if (entityEvent != null && entityEvent.getEntity() != null) {
                    center = entityEvent.getEntity().getLocation();
                }
            }
        }
        if (center == null || center.getWorld() == null) return ActionResult.ALLOW;

        Entity eventTarget = null;
        Object nativeEvent = ctx.nativeEvent();
        if (nativeEvent instanceof EntityDamageByEntityEvent hit) {
            eventTarget = hit.getEntity();
        } else {
            eventTarget = ctx.target(Entity.class);
        }

        Double baseDamage = null;
        if (amount != null) {
            baseDamage = amount;
        } else if (multiplier != null) {
            if (nativeEvent instanceof EntityDamageEvent ede) {
                baseDamage = ede.getDamage() * multiplier;
            } else {
                Object dmgObj = ctx.getValue("EVENT.damage");
                if (dmgObj instanceof Number n) {
                    baseDamage = n.doubleValue() * multiplier;
                }
            }
        }
        if (baseDamage == null || baseDamage <= 0) return ActionResult.ALLOW;

        Set<EntityType> includeTypes = new HashSet<>();
        if (entityTypeObj instanceof String s && !s.isBlank()) {
            EntityType type = Resolvers.resolveEntityType(s);
            if (type != null) includeTypes.add(type);
        }
        if (entityTypesObj instanceof List<?> list) {
            for (Object o : list) {
                if (o instanceof String s && !s.isBlank()) {
                    EntityType type = Resolvers.resolveEntityType(s);
                    if (type != null) includeTypes.add(type);
                }
            }
        }

        Set<EntityType> excludeTypes = new HashSet<>();
        if (excludeTypesObj instanceof List<?> list) {
            for (Object o : list) {
                if (o instanceof String s && !s.isBlank()) {
                    EntityType type = Resolvers.resolveEntityType(s);
                    if (type != null) excludeTypes.add(type);
                }
            }
        } else if (excludeTypesObj instanceof String s && !s.isBlank()) {
            EntityType type = Resolvers.resolveEntityType(s);
            if (type != null) excludeTypes.add(type);
        }

        Entity casterFinal = caster;
        Entity targetFinal = eventTarget;
        double dmgFinal = baseDamage;
        double rFinal = Math.max(0.0, radius);
        Location centerFinal = center.clone();

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (centerFinal.getWorld() == null) return;

            final String aoeTag = "rd_internal:aoe_damage";
            boolean tagged = false;
            try {
                if (casterFinal != null) {
                    casterFinal.addScoreboardTag(aoeTag);
                    tagged = true;
                }
            } catch (Exception ignored) {
                tagged = false;
            }

            try {
                for (Entity entity : centerFinal.getWorld().getNearbyEntities(centerFinal, rFinal, rFinal, rFinal)) {
                    if (excludeCaster && casterFinal != null && entity == casterFinal) continue;
                    if (excludeEventTarget && targetFinal != null && entity == targetFinal) continue;
                    if (!includeTypes.isEmpty() && !includeTypes.contains(entity.getType())) continue;
                    if (!excludeTypes.isEmpty() && excludeTypes.contains(entity.getType())) continue;
                    if (entity instanceof LivingEntity living) {
                        if (casterFinal != null) {
                            living.damage(dmgFinal, casterFinal);
                        } else {
                            living.damage(dmgFinal);
                        }
                    }
                }
            } finally {
                try {
                    if (tagged && casterFinal != null) {
                        casterFinal.removeScoreboardTag(aoeTag);
                    }
                } catch (Exception ignored) {
                }
            }
        });

        return ActionResult.ALLOW;
    }

    private static Object firstNonNull(Map<?, ?> raw, String... keys) {
        for (String k : keys) {
            if (raw.containsKey(k) && raw.get(k) != null) {
                return raw.get(k);
            }
        }
        return null;
    }
}
