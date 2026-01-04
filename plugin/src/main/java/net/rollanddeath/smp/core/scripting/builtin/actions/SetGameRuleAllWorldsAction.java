package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Bukkit;
import org.bukkit.World;

final class SetGameRuleAllWorldsAction {
    private SetGameRuleAllWorldsAction() {
    }

    static void register() {
        ActionRegistrar.register("set_gamerule_all_worlds", SetGameRuleAllWorldsAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String rule = Resolvers.string(null, raw, "rule");
        Object valueObj = raw.get("value");
        if (rule == null || rule.isBlank() || valueObj == null) return null;
        
        return ctx -> {
            String valStr = String.valueOf(valueObj);
            ActionUtils.runSync(ctx.plugin(), () -> {
                for (World w : Bukkit.getWorlds()) {
                    w.setGameRuleValue(rule, valStr);
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
