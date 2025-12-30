package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class SetBlockBelowTypeAction {
    private SetBlockBelowTypeAction() {
    }

    static void register() {
        ActionRegistrar.register("set_block_below_type", SetBlockBelowTypeAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String material = Resolvers.string(null, raw, "material");
        if (material == null || material.isBlank()) return null;
        return BuiltInActions.setBlockBelowType(material);
    }
}
