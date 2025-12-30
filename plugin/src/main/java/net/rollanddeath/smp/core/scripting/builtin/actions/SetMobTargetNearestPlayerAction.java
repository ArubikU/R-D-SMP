package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

final class SetMobTargetNearestPlayerAction {
    private SetMobTargetNearestPlayerAction() {}

    static void register() {
        ActionRegistrar.register("set_mob_target_nearest_player", SetMobTargetNearestPlayerAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Integer radius = Resolvers.integer(null, raw, "radius");
        int r = radius != null ? Math.max(1, radius) : 20;

        return ctx -> {
            Mob mob = ctx.subjectOrEventEntity(Mob.class);
            if (mob == null) return ActionResult.ALLOW;
            
            ActionUtils.runSync(ctx.plugin(), () -> {
                Player nearest = null;
                double bestDistSq = Double.MAX_VALUE;
                double rSq = r * r;
                
                for (Player p : mob.getWorld().getPlayers()) {
                    if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.getGameMode() == org.bukkit.GameMode.CREATIVE) continue;
                    double dSq = p.getLocation().distanceSquared(mob.getLocation());
                    if (dSq > rSq) continue;
                    if (dSq < bestDistSq) {
                        bestDistSq = dSq;
                        nearest = p;
                    }
                }
                
                if (nearest != null) {
                    mob.setTarget(nearest);
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
