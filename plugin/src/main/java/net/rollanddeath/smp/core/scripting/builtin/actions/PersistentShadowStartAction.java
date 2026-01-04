package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.modifiers.scripted.PersistentShadowService;

final class PersistentShadowStartAction {
    private PersistentShadowStartAction() {}

    static void register() {
        ActionRegistrar.register("persistent_shadow_start", PersistentShadowStartAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Long interval = Resolvers.longVal(null, raw, "interval_ms", "interval");
        Integer check = Resolvers.integer(null, raw, "check_ticks", "period");
        
        long finalInterval = interval != null ? interval : 300_000L;
        int finalCheck = check != null ? check : 100;

        return ctx -> {
            PersistentShadowService service = ctx.plugin().getPersistentShadowService();
            if (service != null) {
                service.start(finalInterval, finalCheck);
            }
            return ActionResult.ALLOW;
        };
    }
}
