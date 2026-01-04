package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Player;

final class SetPlayerRiptidingAction {
    private SetPlayerRiptidingAction() {
    }

    static void register() {
        ActionRegistrar.register("set_player_riptiding", SetPlayerRiptidingAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object valueSpec = Resolvers.plain(raw, "value", "riptiding");
        if (valueSpec == null) return null;
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            Boolean value = Resolvers.bool(ctx, valueSpec);
            if (value == null) return ActionResult.ALLOW;

            ActionUtils.runSync(plugin, () -> {
                try {
                    player.setRiptiding(value);
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }
}