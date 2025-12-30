package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.HashMap;
import java.util.List;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import net.rollanddeath.smp.core.scripting.ScriptPhase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

final class RunLaterAction {
    private RunLaterAction() {
    }

    static void register() {
        ActionRegistrar.register("run_later", RunLaterAction::parse, "run_actions_later", "delay");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Integer delay = Resolvers.integer(null, raw, "delay_ticks", "delay", "ticks");
        if (delay == null || delay < 0) return null;
        List<Action> actions = Resolvers.parseActionList(raw.get("actions"));
        if (actions == null || actions.isEmpty()) return null;
        return ctx -> execute(ctx, delay, actions);
    }

    private static ActionResult execute(ScriptContext ctx, int delayTicks, List<Action> actions) {
        var plugin = ctx.plugin();
        if (plugin == null) return ActionResult.ALLOW;

        int delay = Math.max(0, delayTicks);
        List<Action> safe = actions != null ? actions : List.of();
        if (safe.isEmpty()) return ActionResult.ALLOW;

        Player player = ctx.player();
        String subjectId = ctx.subjectId();
        ScriptPhase phase = ctx.phase();

        HashMap<String, Object> vars = ctx.variables() != null ? new HashMap<>(ctx.variables()) : new HashMap<>();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            try {
                ScriptContext child = new ScriptContext(plugin, player, subjectId, phase, vars);
                ScriptEngine.runAllWithResult(child, safe);
            } catch (Exception ignored) {
            }
        }, delay);

        return ActionResult.ALLOW;
    }
}
