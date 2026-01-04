package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Material;
import org.bukkit.entity.Player;

final class SetPlayerCooldownAction {
    private SetPlayerCooldownAction() {
    }

    static void register() {
        ActionRegistrar.register("set_player_cooldown", SetPlayerCooldownAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object materialSpec = Resolvers.plain(raw, "material");
        Object ticksSpec = Resolvers.plain(raw, "ticks", "cooldown_ticks", "duration_ticks");
        if (materialSpec == null) return null;
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            String matName = Resolvers.string(ctx, materialSpec);
            Material mat = Resolvers.resolveMaterial(matName);
            Integer ticks = Resolvers.integer(ctx, ticksSpec);
            int cooldown = ticks != null ? Math.max(0, ticks) : -1;
            if (mat == null || cooldown < 0) return ActionResult.ALLOW;

            ActionUtils.runSync(plugin, () -> {
                try {
                    player.setCooldown(mat, cooldown);
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }
}