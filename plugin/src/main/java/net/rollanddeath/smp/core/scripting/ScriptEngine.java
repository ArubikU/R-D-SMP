package net.rollanddeath.smp.core.scripting;

import java.util.List;

public final class ScriptEngine {

    private ScriptEngine() {
    }

    public static boolean allConditionsPass(ScriptContext ctx, List<Condition> conditions) {
        if (conditions == null || conditions.isEmpty()) return true;
        for (Condition c : conditions) {
            if (c == null) continue;
            if (!c.test(ctx)) return false;
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
            ActionResult r = a.run(ctx);
            if (r != null && r.deny()) {
                deny = true;
            }
        }
        return deny ? ActionResult.DENY : ActionResult.ALLOW;
    }
}
