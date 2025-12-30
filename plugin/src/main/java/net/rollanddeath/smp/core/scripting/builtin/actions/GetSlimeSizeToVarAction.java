package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.entity.Slime;

/** Stores the size of the slime involved in the context into a variable. */
public final class GetSlimeSizeToVarAction {
    private GetSlimeSizeToVarAction() {
    }

    static void register() {
        ActionRegistrar.register("get_slime_size_to_var", GetSlimeSizeToVarAction::parse, "store_slime_size");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String key = Resolvers.string(null, raw, "key", "store_key", "to", "out");
        if (key == null || key.isBlank()) return null;
        String store = key.trim();
        return ctx -> execute(ctx, store);
    }

    private static ActionResult execute(ScriptContext ctx, String storeKey) {
        Slime slime = ctx.subjectOrEventEntity(Slime.class);
        if (slime == null) return ActionResult.ALLOW;
        ctx.setGenericVarCompat(storeKey, Math.max(1, slime.getSize()));
        return ActionResult.ALLOW;
    }
}
