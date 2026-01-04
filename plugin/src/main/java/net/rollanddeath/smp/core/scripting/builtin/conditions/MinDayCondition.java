package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;

import java.util.Map;

public class MinDayCondition implements Condition {

    private final int value;

    public static void register() {
        ConditionRegistrar.register("min_day", MinDayCondition::new);
    }

    public MinDayCondition(Map<?, ?> spec) {
        Integer v = Resolvers.integer(null, spec, "value", "day");
        this.value = v != null ? v : 1;
    }

    @Override
    public boolean test(ScriptContext ctx) {
        RollAndDeathSMP p = ctx.plugin();
        int currentDay = Math.max(1, p.getGameManager() != null ? p.getGameManager().getCurrentDay() : 1);
        return currentDay >= value;
    }
}
