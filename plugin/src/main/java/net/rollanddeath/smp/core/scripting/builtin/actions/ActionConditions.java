package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.builtin.conditions.ConditionRegistrar;

/** Utilities to wrap actions with YAML condition/when checks. */
final class ActionConditions {
    private ActionConditions() {
    }

    static Action wrap(Map<?, ?> raw, Action base) {
        if (base == null) return null;
        Object condObj = raw != null ? raw.get("condition") : null;
        if (!(condObj instanceof Map<?, ?>)) {
            condObj = raw != null ? raw.get("when") : null;
        }
        if (condObj instanceof Map<?, ?> condMap) {
            Condition cond = ConditionRegistrar.parse(condMap);
            if (cond == null) return null;
            return ctx -> cond.test(ctx) ? base.run(ctx) : ActionResult.ALLOW;
        }
        return base;
    }
}
