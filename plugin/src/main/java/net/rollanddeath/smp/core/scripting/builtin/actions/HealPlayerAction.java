package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

final class HealPlayerAction {
    private HealPlayerAction() {
    }

    static void register() {
        ActionRegistrar.register("heal_player", HealPlayerAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object amountSpec = Resolvers.plain(raw, "amount");
        Object amountKeySpec = Resolvers.plain(raw, "amount_key");
        if (amountSpec == null && amountKeySpec == null) return null;
        return heal(amountSpec, amountKeySpec);
    }

    private static Action heal(Object amountSpec, Object amountKeySpec) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            Double direct = Resolvers.doubleVal(ctx, amountSpec);
            String amountKey = Resolvers.string(ctx, amountKeySpec);

            Double amt = direct;
            if ((amt == null || amt <= 0.0) && amountKey != null && !amountKey.isBlank()) {
                amt = tryGetDouble(ctx.getValue(amountKey));
            }

            double amount = amt != null ? Math.max(0.0, amt) : 0.0;
            if (amount <= 0.0) return ActionResult.ALLOW;

            BuiltInActions.runSync(plugin, () -> {
                try {
                    double max = player.getAttribute(Attribute.MAX_HEALTH) != null ? player.getAttribute(Attribute.MAX_HEALTH).getValue() : player.getMaxHealth();
                    double next = Math.min(player.getHealth() + amount, max);
                    player.setHealth(next);
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    private static Double tryGetDouble(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.doubleValue();
        if (v instanceof String s) {
            try {
                return Double.parseDouble(s.trim());
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
