package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInArgs;
import net.rollanddeath.smp.core.scripting.builtin.actions.ActionRegistrar;
import net.rollanddeath.smp.core.scripting.library.CallGuard;
import net.rollanddeath.smp.core.scripting.library.ScriptLibrary;

final class CallAction {
    private CallAction() {
    }

    static void register() {
        ActionRegistrar.register("call", CallAction::parse,
            "actions_call", "call_actions", "macro");
    }

    private static Action parse(Map<?, ?> raw) {
        String ref = BuiltInArgs.refOrId(raw);
        if (ref == null) return null;

        Map<String, Object> withArgs = BuiltInArgs.stringObjectMap(raw.get("with"));
        boolean allowMissing = BuiltInArgs.bool(raw, "allow_missing", false);

        return ctx -> execute(ctx, ref, withArgs, allowMissing);
    }

    private static ActionResult execute(ScriptContext ctx, String ref, Map<String, Object> withArgs, boolean allowMissing) {
        if (ctx == null || ctx.plugin() == null) return ActionResult.DENY;
        ScriptLibrary lib = ctx.plugin().getScriptLibrary();
        if (lib == null) return allowMissing ? ActionResult.ALLOW : ActionResult.DENY;

        List<Action> block = lib.getActions(ref);
        if (block == null || block.isEmpty()) return allowMissing ? ActionResult.ALLOW : ActionResult.DENY;

        String guardKey = "actions:" + ref;
        if (!CallGuard.enter(guardKey)) {
            ctx.plugin().getLogger().warning("[Script] call(actions) recursivo/cíclico: " + ref + " (subject=" + ctx.subjectId() + ")");
            return ActionResult.DENY;
        }

        try {
            if (withArgs != null && !withArgs.isEmpty()) {
                Map<String, Object> resolved = new HashMap<>();
                for (Map.Entry<String, Object> e : withArgs.entrySet()) {
                    resolved.put(e.getKey(), resolveScriptVars(ctx, e.getValue()));
                }
                BuiltInArgs.applyWithArgs(ctx, resolved);
            } else {
                BuiltInArgs.applyWithArgs(ctx, withArgs);
            }
            return ScriptEngine.runAllWithResult(ctx, block);
        } finally {
            CallGuard.exit(guardKey);
        }
    }

    private static Object resolveScriptVars(ScriptContext ctx, Object value) {
        if (value == null) return null;
        if (value instanceof String s) return Resolvers.resolve(ctx, s);
        if (value instanceof Number || value instanceof Boolean) return value;
        if (value instanceof List<?> list) {
            return list.stream()
                .map(x -> resolveScriptVars(ctx, x))
                .toList();
        }
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> out = new HashMap<>();
            for (Map.Entry<?, ?> e : map.entrySet()) {
                String key = e.getKey() != null ? String.valueOf(e.getKey()) : null;
                out.put(key, resolveScriptVars(ctx, e.getValue()));
            }
            return out;
        }
        return value;
    }
}
