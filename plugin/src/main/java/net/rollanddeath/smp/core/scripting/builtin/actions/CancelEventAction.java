package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.event.Cancellable;

final class CancelEventAction {
    private CancelEventAction() {}

    static void register() {
        ActionRegistrar.register("cancel_event", CancelEventAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        boolean value = raw.get("value") instanceof Boolean b ? b : true;
        return ctx -> {
            if (ctx.nativeEvent() instanceof Cancellable c) {
                c.setCancelled(value);
            }
            return ActionResult.ALLOW;
        };
    }
}
