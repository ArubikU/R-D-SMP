package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class SetPhantomSizeAction {
    private SetPhantomSizeAction() {
    }

    static void register() {
        ActionRegistrar.register("set_phantom_size", SetPhantomSizeAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Integer size = Resolvers.integer(null, raw, "size");
        if (size == null || size < 0) return null;
        Object entitySpec = firstNonNull(raw, "entity", "target", "key", "entity_key");
        return BuiltInActions.setPhantomSize(size, entitySpec);
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
