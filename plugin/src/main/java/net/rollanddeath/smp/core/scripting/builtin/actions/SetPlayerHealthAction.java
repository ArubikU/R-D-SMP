package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;
import net.rollanddeath.smp.integration.PlaceholderUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

final class SetPlayerHealthAction {
    private SetPlayerHealthAction() {
    }

    static void register() {
        ActionRegistrar.register("set_player_health", SetPlayerHealthAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object valueSpec = Resolvers.plain(raw, "value");
        if (valueSpec == null) return null;
        return ctx -> execute(ctx, valueSpec);
    }

    private static ActionResult execute(ScriptContext ctx, Object valueSpec) {
        RollAndDeathSMP plugin = ctx.plugin();
        Player player = ctx.player();
        if (player == null) return ActionResult.ALLOW;

        String rawString = Resolvers.string(ctx, valueSpec);
        Double target = rawString != null && rawString.trim().equalsIgnoreCase("max")
            ? null
            : Resolvers.doubleVal(ctx, valueSpec);

        Double targetFinal = target;
        BuiltInActions.runSync(plugin, () -> {
            try {
                double max;
                try {
                    var inst = player.getAttribute(Attribute.MAX_HEALTH);
                    max = inst != null ? inst.getValue() : player.getMaxHealth();
                } catch (Exception ignored) {
                    max = player.getMaxHealth();
                }

                double desired = (targetFinal == null) ? max : targetFinal;
                if (Double.isNaN(desired) || Double.isInfinite(desired)) return;
                desired = Math.max(0.0, Math.min(max, desired));
                player.setHealth(desired);
            } catch (Exception ignored) {
            }
        });

        return ActionResult.ALLOW;
    }
}
