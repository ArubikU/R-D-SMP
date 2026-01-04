package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.modifiers.scripted.PersistentShadowService;

final class PersistentShadowStopAction {
    private PersistentShadowStopAction() {}

    static void register() {
        ActionRegistrar.register("persistent_shadow_stop", PersistentShadowStopAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        return ctx -> {
            PersistentShadowService service = ctx.plugin().getPersistentShadowService();
            if (service != null) {
                service.stop();
            }
            return ActionResult.ALLOW;
        };
    }
}
