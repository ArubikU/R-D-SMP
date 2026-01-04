package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;

import java.util.Map;

public class VarTruthyCondition implements Condition {

    private final String key;
    private final boolean invert;

    public static void register() {
        ConditionRegistrar.register("var_truthy", VarTruthyCondition::new);
    }

    public VarTruthyCondition(Map<?, ?> spec) {
        this.key = Resolvers.string(null, spec, "key", "var");
        this.invert = Resolvers.bool(null, spec.get("invert")) == Boolean.TRUE;
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (key == null) return false;
        boolean truthy = Resolvers.bool(ctx, ctx.getValue(key)) == Boolean.TRUE;
        return invert ? !truthy : truthy;
    }
}
