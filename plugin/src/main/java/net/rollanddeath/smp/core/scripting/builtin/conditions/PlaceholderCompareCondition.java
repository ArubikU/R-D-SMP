package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.integration.PlaceholderUtil;

import java.util.Map;

public class PlaceholderCompareCondition implements Condition {

    private final String placeholder;
    private final String operator;
    private final Object valueSpec;
    private final boolean caseInsensitive;

    public static void register() {
        ConditionRegistrar.register("placeholder_compare", PlaceholderCompareCondition::new);
    }

    public PlaceholderCompareCondition(Map<?, ?> spec) {
        this.placeholder = Resolvers.string(null, spec, "placeholder", "papi");
        this.operator = Resolvers.string(null, spec, "operator", "op");
        this.valueSpec = spec.get("value");
        this.caseInsensitive = Resolvers.bool(null, spec.get("case_insensitive")) != Boolean.FALSE;
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (placeholder == null) return false;
        
        String left = PlaceholderUtil.resolvePlaceholders(ctx.plugin(), ctx.player(), placeholder);
        Object right = Resolvers.resolve(ctx, valueSpec);

        return VarCompareCondition.compare(left, right, operator, caseInsensitive);
    }
}
