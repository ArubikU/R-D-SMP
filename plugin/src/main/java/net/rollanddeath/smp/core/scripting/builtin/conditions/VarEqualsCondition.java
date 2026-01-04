package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class VarEqualsCondition implements Condition {

    private final String key;
    private final Object valueSpec;
    private final boolean caseInsensitive;

    public static void register() {
        ConditionRegistrar.register("var_equals", VarEqualsCondition::new);
    }

    public VarEqualsCondition(Map<?, ?> spec) {
        this.key = Resolvers.string(null, spec, "key", "var");
        this.valueSpec = spec.get("value");
        this.caseInsensitive = Resolvers.bool(null, spec.get("case_insensitive")) != Boolean.FALSE; // default true
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (key == null) return false;
        Object actual = ctx.getValue(key);
        Object expected = Resolvers.resolve(ctx, valueSpec);

        if (actual == null) return expected == null;
        if (expected == null) return false;

        if (caseInsensitive && actual instanceof String s1 && expected instanceof String s2) {
            return s1.equalsIgnoreCase(s2);
        }

        return Objects.equals(actual, expected);
    }
}
