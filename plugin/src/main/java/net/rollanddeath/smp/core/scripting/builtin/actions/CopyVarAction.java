package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.integration.PlaceholderUtil;
import org.bukkit.entity.Player;

final class CopyVarAction {
    private CopyVarAction() {
    }

    static void register() {
        ActionRegistrar.register("copy_var", CopyVarAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object fromSpec = Resolvers.plain(raw, "from");
        Object toSpec = Resolvers.plain(raw, "to");
        Object defSpec = Resolvers.plain(raw, "default");
        String from = Resolvers.string(null, fromSpec);
        String to = Resolvers.string(null, toSpec);
        if (from == null || from.isBlank() || to == null || to.isBlank()) return null;
        return ctx -> execute(ctx, from, to, defSpec);
    }

    private static ActionResult execute(ScriptContext ctx, String fromKey, String toKey, Object defaultValue) {
        RollAndDeathSMP plugin = ctx.plugin();
        Player player = ctx.player();

        Object v = ctx.getValue(fromKey);
        if (v == null) {
            v = Resolvers.object(ctx, defaultValue);
        }
        if (v instanceof String s) {
            v = PlaceholderUtil.resolvePlaceholders(plugin, player, s);
        }
        ctx.setGenericVarCompat(toKey, v);
        return ActionResult.ALLOW;
    }
}
