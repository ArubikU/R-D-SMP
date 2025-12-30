package net.rollanddeath.smp.core.scripting;

import java.util.List;

public final class ScriptEngine {

    private ScriptEngine() {
    }

    public static boolean allConditionsPass(ScriptContext ctx, List<Condition> conditions) {
        if (conditions == null || conditions.isEmpty()) return true;
        for (Condition c : conditions) {
            if (c == null) continue;
            try {
                if (!c.test(ctx)) return false;
            } catch (Exception e) {
                if (ctx != null && ctx.plugin() != null) {
                    ctx.plugin().getLogger().warning("[ScriptEngine] Error evaluando condición en '" + ctx.subjectId() + "': " + e.getMessage());
                }
                return false;
            }
        }
        return true;
    }

    public static void runAll(ScriptContext ctx, List<Action> actions) {
        runAllWithResult(ctx, actions);
    }

    public static ActionResult runAllWithResult(ScriptContext ctx, List<Action> actions) {
        if (actions == null || actions.isEmpty()) return ActionResult.ALLOW;

        boolean deny = false;
        for (Action a : actions) {
            if (a == null) continue;
            try {
                ActionResult r = a.run(ctx);
                if (r != null && r.deny()) {
                    deny = true;
                }
            } catch (Exception e) {
                if (ctx != null && ctx.plugin() != null) {
                    ctx.plugin().getLogger().warning("[ScriptEngine] Error ejecutando acción en '" + ctx.subjectId() + "': " + e.getMessage());
                }
                // Fail-safe: si una acción explota, tratamos como DENY para evitar bypass.
                deny = true;
            }
        }
        return deny ? ActionResult.DENY : ActionResult.ALLOW;
    }
}
