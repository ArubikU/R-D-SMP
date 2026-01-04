package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.event.player.PlayerExpChangeEvent;

final class MultiplyEventExpAction {
    private MultiplyEventExpAction() {}

    static void register() {
        ActionRegistrar.register("multiply_event_exp", MultiplyEventExpAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Double multiplier = Resolvers.doubleVal(null, raw, "multiplier", "mult");
        double m = multiplier != null ? multiplier : 1.0;

        return ctx -> {
            if (ctx.event() instanceof PlayerExpChangeEvent e) {
                e.setAmount((int)(e.getAmount() * m));
            }
            return ActionResult.ALLOW;
        };
    }
}
