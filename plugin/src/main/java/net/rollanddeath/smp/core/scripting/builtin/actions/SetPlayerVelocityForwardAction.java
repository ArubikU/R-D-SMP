package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

final class SetPlayerVelocityForwardAction {
    private SetPlayerVelocityForwardAction() {
    }

    static void register() {
        ActionRegistrar.register("set_player_velocity_forward", SetPlayerVelocityForwardAction::parse, "dash_forward");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object multiplierSpec = Resolvers.plain(raw, "multiplier", "speed", "strength");
        if (multiplierSpec == null) return null;
        Object ySpec = Resolvers.plain(raw, "y");
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            Double multiplier = Resolvers.doubleVal(ctx, multiplierSpec);
            if (multiplier == null) return ActionResult.ALLOW;
            double mult = multiplier;

            Double yOverride = Resolvers.doubleVal(ctx, ySpec);

            ActionUtils.runSync(plugin, () -> {
                try {
                    Vector dir = player.getEyeLocation().getDirection();
                    if (dir.lengthSquared() > 1.0e-6) {
                        dir.normalize();
                    }
                    Vector v = dir.multiply(mult);
                    if (yOverride != null) {
                        v.setY(v.getY() + yOverride);
                    }
                    player.setVelocity(v);
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }
}