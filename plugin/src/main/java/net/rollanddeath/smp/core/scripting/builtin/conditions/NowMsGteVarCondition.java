package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;

import java.util.Map;

public class NowMsGteVarCondition implements Condition {

    private final String key;

    public static void register() {
        ConditionRegistrar.register("now_ms_gte_var", NowMsGteVarCondition::new);
    }

    public NowMsGteVarCondition(Map<?, ?> spec) {
        this.key = Resolvers.string(null, spec, "key", "var");
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (key == null) return true;
        Object val = ctx.getValue(key);
        if (val == null) return true;

        long target;
        if (val instanceof Number n) {
            target = n.longValue();
        } else {
            try {
                target = Long.parseLong(String.valueOf(val));
            } catch (Exception ignored) {
                return true;
            }
        }
        return System.currentTimeMillis() >= target;
    }
}
