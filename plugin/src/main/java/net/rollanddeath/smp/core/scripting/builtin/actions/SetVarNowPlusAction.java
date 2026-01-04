package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ActionResult;
import java.util.concurrent.ThreadLocalRandom;

final class SetVarNowPlusAction {
    private SetVarNowPlusAction() {
    }

    static void register() {
        ActionRegistrar.register("set_var_now_plus", SetVarNowPlusAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String key = Resolvers.string(null, raw, "key");
        if (key == null || key.isBlank()) return null;
        Integer minMs = Resolvers.integer(null, raw, "min_ms");
        Integer maxMs = Resolvers.integer(null, raw, "max_ms");
        Integer addMs = Resolvers.integer(null, raw, "add_ms");
        int min = minMs != null ? minMs : (addMs != null ? addMs : 0);
        int max = maxMs != null ? maxMs : (addMs != null ? addMs : min);
        
        return ctx -> {
            long now = System.currentTimeMillis();
            long offset;
            if (min == max) {
                offset = min;
            } else {
                offset = ThreadLocalRandom.current().nextLong(min, max + 1);
            }
            ctx.setGenericVarCompat(key, now + offset);
            return ActionResult.ALLOW;
        };
    }
}
