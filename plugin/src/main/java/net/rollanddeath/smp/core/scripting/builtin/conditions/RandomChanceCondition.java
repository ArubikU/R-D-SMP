package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RandomChanceCondition implements Condition {

    private final double probability;

    public static void register() {
        ConditionRegistrar.register("random_chance", RandomChanceCondition::new);
    }

    public RandomChanceCondition(Map<?, ?> spec) {
        Double p = null;
        Object pObj = spec.get("probability");
        if (pObj instanceof Number n) p = n.doubleValue();
        else if (pObj instanceof String s) {
            try {
                p = Double.parseDouble(s.trim());
            } catch (Exception ignored) {}
        }

        if (p == null) {
            Integer num = Resolvers.integer(null, spec, "numerator");
            Integer den = Resolvers.integer(null, spec, "denominator");
            if (num != null && den != null && den > 0) {
                p = Math.max(0.0, Math.min(1.0, num.doubleValue() / den.doubleValue()));
            }
        }
        this.probability = p != null ? Math.max(0.0, Math.min(1.0, p)) : 0.0;
    }

    @Override
    public boolean test(ScriptContext ctx) {
        return ThreadLocalRandom.current().nextDouble() < probability;
    }
}
