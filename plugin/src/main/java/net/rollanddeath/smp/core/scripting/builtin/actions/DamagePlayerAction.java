package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;
import org.bukkit.entity.Player;

final class DamagePlayerAction {
    private DamagePlayerAction() {
    }

    static void register() {
        ActionRegistrar.register("damage_player", DamagePlayerAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object amountSpec = Resolvers.plain(raw, "amount");
        if (amountSpec == null) return null;
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            Double amount = Resolvers.doubleVal(ctx, amountSpec);
            double dmg = amount != null ? Math.max(0.0, amount) : 0.0;
            if (dmg <= 0.0) return ActionResult.ALLOW;

            BuiltInActions.runSync(plugin, () -> player.damage(dmg));
            return ActionResult.ALLOW;
        };
    }
}