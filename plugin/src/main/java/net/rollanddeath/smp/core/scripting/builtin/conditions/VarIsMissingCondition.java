package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;

import java.util.Map;

public class VarIsMissingCondition implements Condition {

    private final String key;

    public static void register() {
        ConditionRegistrar.register("var_is_missing", VarIsMissingCondition::new);
    }

    public VarIsMissingCondition(Map<?, ?> spec) {
        this.key = Resolvers.string(null, spec, "key", "var");
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (key == null) return true;
        Object v = ctx.getValue(key);
        if (v == null) return true;
        if (v instanceof String s) return s.isBlank();
        return false;
    }
}
