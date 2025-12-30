package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;

final class ForInAction {
    private ForInAction() {}

    static void register() {
        ActionRegistrar.register("for_in", ForInAction::parse, "foreach", "iterate");
    }

    private static Action parse(Map<?, ?> raw) {
        Object itemsSpec = raw.get("items");
        if (itemsSpec == null) itemsSpec = raw.get("list");
        if (itemsSpec == null) itemsSpec = raw.get("array");
        
        String varName = Resolvers.string(null, raw, "var", "as", "key");
        if (varName == null || varName.isBlank()) varName = "item";
        
        List<Action> actions = Resolvers.parseActionList(raw.get("actions"));
        if (actions == null || actions.isEmpty()) return null;

        final String finalVarName = varName;
        final Object finalItemsSpec = itemsSpec;

        return ctx -> {
            Object resolved = Resolvers.resolve(ctx, finalItemsSpec);
            if (resolved == null) return ActionResult.ALLOW;

            List<?> list = null;
            if (resolved instanceof List<?> l) {
                list = l;
            } else if (resolved.getClass().isArray()) {
                list = new java.util.ArrayList<>();
                int len = java.lang.reflect.Array.getLength(resolved);
                for (int i = 0; i < len; i++) {
                    ((List<Object>) list).add(java.lang.reflect.Array.get(resolved, i));
                }
            } else {
                // Single item treated as list of 1
                list = List.of(resolved);
            }

            if (list.isEmpty()) return ActionResult.ALLOW;

            for (Object item : list) {
                Map<String, Object> vars = ctx.variables() != null ? new HashMap<>(ctx.variables()) : new HashMap<>();
                vars.put(finalVarName, item);
                
                // Also expose "loop_index"? Maybe later.

                ScriptContext child = new ScriptContext(ctx.plugin(), ctx.player(), ctx.subjectId(), ctx.phase(), vars);
                ActionResult res = ScriptEngine.runAllWithResult(child, actions);
                if (res == ActionResult.DENY) return ActionResult.DENY; // Propagate deny? Usually loops don't propagate unless explicit break/return
                // For now, let's assume we continue unless something throws or we want to support break.
                // But ActionResult.DENY usually means "cancel event". If a loop action cancels, should we stop looping?
                // Probably yes if it's a "cancel event" signal.
            }

            return ActionResult.ALLOW;
        };
    }
}
