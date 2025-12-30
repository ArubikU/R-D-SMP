package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;
import org.bukkit.entity.Player;

final class DamagePlayerOrKillAction {
    private DamagePlayerOrKillAction() {
    }

    static void register() {
        ActionRegistrar.register("damage_player_or_kill", DamagePlayerOrKillAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object damageSpec = Resolvers.plain(raw, "damage", "amount", "dmg");
        Object thresholdSpec = Resolvers.plain(raw, "kill_if_health_at_most", "kill_at", "health_threshold");
        if (damageSpec == null) return null;
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            Double damage = Resolvers.doubleVal(ctx, damageSpec);
            Double threshold = Resolvers.doubleVal(ctx, thresholdSpec);

            double dmg = damage != null ? Math.max(0.0, damage) : 0.0;
            double th = threshold != null ? Math.max(0.0, threshold) : 1.0;
            if (dmg <= 0.0) return ActionResult.ALLOW;

            BuiltInActions.runSync(plugin, () -> {
                try {
                    double hp = player.getHealth();
                    if (hp > th) {
                        player.damage(dmg);
                    } else {
                        player.damage(1000.0);
                    }
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }
}