package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class SetBlockTypeAtAction {
    private SetBlockTypeAtAction() {
    }

    static void register() {
        ActionRegistrar.register("set_block_type_at", SetBlockTypeAtAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object where = firstNonNull(raw, "where", "location", "key", "location_key");
        String material = Resolvers.string(null, raw, "material");
        if (where == null || material == null || material.isBlank()) return null;
        boolean applyPhysics = raw.get("apply_physics") instanceof Boolean b ? b : true;
        return BuiltInActions.setBlockTypeAt(where, material, applyPhysics);
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
