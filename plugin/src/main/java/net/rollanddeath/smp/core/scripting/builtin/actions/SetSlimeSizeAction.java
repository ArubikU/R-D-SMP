package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class SetSlimeSizeAction {
    private SetSlimeSizeAction() {
    }

    static void register() {
        ActionRegistrar.register("set_slime_size", SetSlimeSizeAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object sizeSpec = firstNonNull(raw, "size", "value");
        String legacyKey = Resolvers.string(null, raw, "size_key", "value_key", "key");
        if (sizeSpec == null && (legacyKey == null || legacyKey.isBlank())) return null;
        Object spec = sizeSpec != null ? sizeSpec : legacyKey;
        return BuiltInActions.setSlimeSize(spec);
    }

    private static Object firstNonNull(java.util.Map<?, ?> raw, String... keys) {
        if (raw == null || keys == null) return null;
        for (String k : keys) {
            if (k == null || k.isBlank()) continue;
            Object v = raw.get(k);
            if (v != null) return v;
        }
        return null;
    }
}
