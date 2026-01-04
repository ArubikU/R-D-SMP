package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.ScriptContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllOfCondition implements Condition {

    private final List<Condition> conditions;

    public static void register() {
        ConditionRegistrar.register("all_of", AllOfCondition::new);
    }

    public AllOfCondition(Map<?, ?> spec) {
        this.conditions = new ArrayList<>();
        Object condsObj = spec.get("conditions");
        if (condsObj instanceof List<?> list) {
            for (Object o : list) {
                if (o instanceof Map<?, ?> m) {
                    Condition c = ConditionRegistrar.parse(m);
                    if (c != null) conditions.add(c);
                }
            }
        }
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (conditions.isEmpty()) return false;
        for (Condition c : conditions) {
            if (!c.test(ctx)) return false;
        }
        return true;
    }
}
