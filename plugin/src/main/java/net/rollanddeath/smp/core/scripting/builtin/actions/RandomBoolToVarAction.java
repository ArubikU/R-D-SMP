package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class RandomBoolToVarAction {
    private RandomBoolToVarAction() {
    }

    static void register() {
        ActionRegistrar.register("random_bool_to_var", RandomBoolToVarAction::parse, "random_boolean_to_var");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String key = Resolvers.string(null, raw, "key", "store_key", "to", "out");
        if (key == null || key.isBlank()) return null;
        Double p = Resolvers.doubleVal(null, raw, "probability");
        if (p == null) p = Resolvers.doubleVal(null, raw, "p");
        double probability = p != null ? p : 0.5;
        return BuiltInActions.randomBoolToVar(key, probability);
    }
}
