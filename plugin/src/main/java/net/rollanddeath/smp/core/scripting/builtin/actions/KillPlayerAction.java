package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;
import org.bukkit.entity.Player;

final class KillPlayerAction {
    private KillPlayerAction() {
    }

    static void register() {
        ActionRegistrar.register("kill_player", KillPlayerAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            BuiltInActions.runSync(plugin, () -> player.setHealth(0.0));
            return ActionResult.ALLOW;
        };
    }
}