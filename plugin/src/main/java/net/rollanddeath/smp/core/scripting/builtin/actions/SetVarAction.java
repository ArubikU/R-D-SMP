package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;
import net.rollanddeath.smp.integration.PlaceholderUtil;
import org.bukkit.entity.Player;

final class SetVarAction {
    private SetVarAction() {
    }

    static void register() {
        ActionRegistrar.register("set_var", SetVarAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object keySpec = Resolvers.plain(raw, "key");
        Object valueSpec = Resolvers.plain(raw, "value");
        String key = Resolvers.string(null, keySpec);
        if (key == null || key.isBlank()) return null;
        return ctx -> execute(ctx, key, valueSpec);
    }

    private static ActionResult execute(ScriptContext ctx, String key, Object valueSpec) {
        RollAndDeathSMP plugin = ctx.plugin();
        Player player = ctx.player();

        Object v = Resolvers.object(ctx, valueSpec);
        if (v instanceof String s) {
            v = PlaceholderUtil.resolvePlaceholders(plugin, player, s);
        }

        ctx.setGenericVarCompat(key, v);
        return ActionResult.ALLOW;
    }
}
