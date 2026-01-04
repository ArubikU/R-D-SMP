package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VarInCondition implements Condition {

    private final String key;
    private final List<Object> values;
    private final boolean caseInsensitive;

    public static void register() {
        ConditionRegistrar.register("var_in", VarInCondition::new);
    }

    public VarInCondition(Map<?, ?> spec) {
        this.key = Resolvers.string(null, spec, "key", "var");
        Object valObj = spec.get("values");
        if (valObj instanceof List<?> l) {
            this.values = (List<Object>) l;
        } else {
            this.values = List.of();
        }
        this.caseInsensitive = Resolvers.bool(null, spec.get("case_insensitive")) != Boolean.FALSE; // default true
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (key == null || values.isEmpty()) return false;
        Object actual = ctx.getValue(key);
        if (actual == null) return false;

        for (Object candidate : values) {
            Object resolved = Resolvers.resolve(ctx, candidate);
            if (caseInsensitive && actual instanceof String s1 && resolved instanceof String s2) {
                if (s1.equalsIgnoreCase(s2)) return true;
            } else if (java.util.Objects.equals(actual, resolved)) {
                return true;
            }
        }
        return false;
    }
}
