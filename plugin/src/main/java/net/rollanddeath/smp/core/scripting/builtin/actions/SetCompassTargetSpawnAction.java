package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.entity.Player;

final class SetCompassTargetSpawnAction {
    private SetCompassTargetSpawnAction() {
    }

    static void register() {
        ActionRegistrar.register("set_compass_target_spawn", SetCompassTargetSpawnAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        return ctx -> {
            Player p = ctx.player();
            if (p != null) {
                p.setCompassTarget(p.getWorld().getSpawnLocation());
            }
            return ActionResult.ALLOW;
        };
    }
}
