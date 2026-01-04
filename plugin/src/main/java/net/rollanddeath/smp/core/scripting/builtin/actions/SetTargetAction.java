package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

final class SetTargetAction {
    private SetTargetAction() {}

    static void register() {
        ActionRegistrar.register("set_target", SetTargetAction::parse, "set_mob_target", "mob_target");
    }

    private static Action parse(Map<?, ?> raw) {
        Object targetSpecRaw = raw.get("target"); // The mob
        if (targetSpecRaw == null) targetSpecRaw = raw.get("entity");
        final Object targetSpec = targetSpecRaw;
        
        Object toSpecRaw = raw.get("to"); // The target entity
        if (toSpecRaw == null) toSpecRaw = raw.get("value");
        final Object toSpec = toSpecRaw;
        
        // Legacy support for set_mob_target_nearest_player
        Double radius = Resolvers.doubleVal(null, raw, "radius", "r");
        boolean nearestPlayer = raw.containsKey("radius") && toSpec == null;

        return ctx -> {
            List<Entity> resolvedMobs = Resolvers.entities(ctx, targetSpec);
            final List<Entity> mobs;
            if (resolvedMobs.isEmpty()) {
                if (targetSpec == null && ctx.subject() != null) {
                    mobs = List.of(ctx.subject());
                } else {
                    return ActionResult.ALLOW;
                }
            } else {
                mobs = resolvedMobs;
            }

            final Entity targetEntity;
            if (nearestPlayer && radius != null) {
                // Logic to find nearest player relative to the mob?
                // Or relative to context location?
                // Usually relative to the mob.
                // We'll handle this inside the loop if needed, but Resolvers.selectEntities is better.
                // If toSpec is null and we have radius, we assume nearest player.
                targetEntity = null;
            } else {
                targetEntity = Resolvers.entity(ctx, toSpec);
            }

            final Double finalRadius = radius;

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity e : mobs) {
                    if (e instanceof Mob mob) {
                        if (nearestPlayer && finalRadius != null) {
                            // Find nearest player
                            double rSq = finalRadius * finalRadius;
                            org.bukkit.entity.Player nearest = null;
                            double minSq = Double.MAX_VALUE;
                            for (org.bukkit.entity.Player p : e.getWorld().getPlayers()) {
                                double dSq = p.getLocation().distanceSquared(e.getLocation());
                                if (dSq <= rSq && dSq < minSq) {
                                    minSq = dSq;
                                    nearest = p;
                                }
                            }
                            mob.setTarget(nearest);
                        } else {
                            if (targetEntity instanceof LivingEntity le) {
                                mob.setTarget(le);
                            } else {
                                mob.setTarget(null);
                            }
                        }
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }
}
