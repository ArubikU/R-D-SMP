package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.ScriptContext;

import java.util.Map;

public class NotCondition implements Condition {

    private final Condition condition;

    public static void register() {
        ConditionRegistrar.register("not", NotCondition::new);
    }

    public NotCondition(Map<?, ?> spec) {
        Object innerObj = spec.get("condition");
        if (innerObj instanceof Map<?, ?> m) {
            this.condition = ConditionRegistrar.parse(m);
        } else {
            this.condition = null;
        }
    }

    @Override
    public boolean test(ScriptContext ctx) {
        return condition != null && !condition.test(ctx);
    }
}
