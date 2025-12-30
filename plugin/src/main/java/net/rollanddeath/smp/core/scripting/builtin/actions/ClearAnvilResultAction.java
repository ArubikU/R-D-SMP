package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.event.inventory.PrepareAnvilEvent;

/** Clears the result slot in an anvil prepare event. */
public final class ClearAnvilResultAction {
    private ClearAnvilResultAction() {
    }

    static void register() {
        ActionRegistrar.register("clear_anvil_result", ClearAnvilResultAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        return ClearAnvilResultAction::execute;
    }

    private static ActionResult execute(ScriptContext ctx) {
        PrepareAnvilEvent anvilEvent = ctx.nativeEvent(PrepareAnvilEvent.class);
        if (anvilEvent != null) {
            anvilEvent.setResult(null);
        }
        return ActionResult.ALLOW;
    }
}
