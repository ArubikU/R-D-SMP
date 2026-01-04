package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

final class ForEachOnlinePlayerAction {
    private ForEachOnlinePlayerAction() {
    }

    static void register() {
        ActionRegistrar.register("for_each_online_player", ForEachOnlinePlayerAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        List<Action> actions = Resolvers.parseActionList(raw.get("actions"));
        if (actions == null || actions.isEmpty()) return null;
        return ctx -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                ScriptContext subCtx = new ScriptContext(
                    ctx.plugin(),
                    p,
                    ctx.subjectId(),
                    ctx.phase(),
                    ctx.variables()
                );
                ScriptEngine.runAllWithResult(subCtx, actions);
            }
            return ActionResult.ALLOW;
        };
    }
}
