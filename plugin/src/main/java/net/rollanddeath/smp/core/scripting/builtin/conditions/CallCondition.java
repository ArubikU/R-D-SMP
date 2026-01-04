package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.library.CallGuard;
import net.rollanddeath.smp.core.scripting.library.ScriptLibrary;

import java.util.HashMap;
import java.util.Map;

public class CallCondition implements Condition {

    private final String ref;
    private final Map<String, Object> withArgs;
    private final boolean allowMissing;

    public static void register() {
        ConditionRegistrar.register("call", CallCondition::new, "cond_call", "call_condition", "macro");
    }

    public CallCondition(Map<?, ?> spec) {
        this.ref = Resolvers.string(null, spec, "ref", "id", "name");
        Object withObj = spec.get("with");
        if (withObj instanceof Map<?, ?> m) {
            this.withArgs = new HashMap<>();
            for (Map.Entry<?, ?> e : m.entrySet()) {
                if (e.getKey() != null) {
                    withArgs.put(String.valueOf(e.getKey()), e.getValue());
                }
            }
        } else {
            this.withArgs = null;
        }
        this.allowMissing = Resolvers.bool(null, spec.get("allow_missing")) == Boolean.TRUE;
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (ref == null || ctx == null || ctx.plugin() == null) return false;
        ScriptLibrary lib = ctx.plugin().getScriptLibrary();
        if (lib == null) return allowMissing;

        Condition c = lib.getCondition(ref);
        if (c == null) return allowMissing;

        String guardKey = "cond:" + ref;
        if (!CallGuard.enter(guardKey)) {
            ctx.plugin().getLogger().warning("[Script] call(condition) recursivo/cíclico: " + ref + " (subject=" + ctx.subjectId() + ")");
            return false;
        }

        try {
            if (withArgs != null) {
                for (Map.Entry<String, Object> e : withArgs.entrySet()) {
                    Object val = Resolvers.resolve(ctx, e.getValue());
                    ctx.setGenericVarCompat(e.getKey(), val);
                }
            }
            return c.test(ctx);
        } finally {
            CallGuard.exit(guardKey);
        }
    }
}
