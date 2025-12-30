package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;

final class AddVarAction {
    private AddVarAction() {
    }

    static void register() {
        ActionRegistrar.register("add_var", AddVarAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object keySpec = Resolvers.plain(raw, "key");
        Object valueSpec = Resolvers.plain(raw, "value");
        String key = Resolvers.string(null, keySpec);
        if (key == null || key.isBlank() || valueSpec == null) return null;
        return ctx -> execute(ctx, key, valueSpec);
    }

    private static ActionResult execute(ScriptContext ctx, String key, Object valueSpec) {
        Object current = ctx.getValue(key);
        Double base = Resolvers.doubleVal(ctx, current);
        Double add = Resolvers.doubleVal(ctx, valueSpec);
        if (base == null) base = 0.0;
        if (add == null) add = 0.0;
        ctx.setGenericVarCompat(key, base + add);
        return ActionResult.ALLOW;
    }
}
