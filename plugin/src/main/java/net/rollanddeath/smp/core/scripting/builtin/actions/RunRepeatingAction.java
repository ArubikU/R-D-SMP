package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import net.rollanddeath.smp.core.scripting.ScriptPhase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

final class RunRepeatingAction {
    private RunRepeatingAction() {
    }

    static void register() {
        ActionRegistrar.register("run_repeating", RunRepeatingAction::parse, "repeat");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object intervalSpec = Resolvers.plain(raw, "interval_ticks", "interval", "period_ticks");
        Object totalSpec = Resolvers.plain(raw, "total_ticks", "duration_ticks", "ticks");
        List<Action> actions = Resolvers.parseActionList(raw.get("actions"));
        if (actions == null || actions.isEmpty()) return null;
        return ctx -> execute(ctx, intervalSpec, totalSpec, actions);
    }

    private static ActionResult execute(ScriptContext ctx, Object intervalSpec, Object totalSpec, List<Action> actions) {
        var plugin = ctx.plugin();
        if (plugin == null) return ActionResult.ALLOW;
        if (actions.isEmpty()) return ActionResult.ALLOW;

        Integer intervalVal = Resolvers.integer(ctx, intervalSpec);
        Integer totalVal = Resolvers.integer(ctx, totalSpec);
        if (intervalVal == null || intervalVal < 1) return ActionResult.ALLOW;
        if (totalVal == null || totalVal < 1) return ActionResult.ALLOW;

        int interval = Math.max(1, intervalVal);
        int total = Math.max(1, totalVal);

        Map<String, Object> base = ctx.variables() != null ? new HashMap<>(ctx.variables()) : new HashMap<>();
        Player player = ctx.player();
        String subjectId = ctx.subjectId();
        ScriptPhase phase = ctx.phase();

        Map<String, Object> eventGenericRaw;
        try {
            var eventScope = ctx.scopes().get(net.rollanddeath.smp.core.scripting.scope.ScopeId.EVENT);
            if (eventScope != null && eventScope.storage() != null) {
                eventGenericRaw = new HashMap<>(eventScope.storage().genericRoot());
            } else {
                eventGenericRaw = new HashMap<>();
            }
        } catch (Exception e) {
            eventGenericRaw = new HashMap<>();
        }
        final Map<String, Object> eventGeneric = eventGenericRaw;
        Object originalEvent = base.get("__event");

        BukkitTask[] task = new BukkitTask[1];
        task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Integer ageObj = base.get("__repeat_age_ticks") instanceof Number n ? n.intValue() : null;
            int age = ageObj != null ? ageObj : 0;
            age += interval;
            base.put("__repeat_age_ticks", age);

            if (age > total) {
                try {
                    task[0].cancel();
                } catch (Exception ignored) {
                }
                return;
            }

            Map<String, Object> vars = new HashMap<>(base);
            Map<String, Object> ev = new HashMap<>();
            ev.put("type", "repeat_tick");
            ev.put("__native", originalEvent);
            ev.put("original", originalEvent);
            ev.put("repeatAgeTicks", age);
            ev.put("repeatTotalTicks", total);
            ev.put("repeatProgress", total > 0 ? (age / (double) total) : 1.0);
            if (!eventGeneric.isEmpty()) {
                ev.put("custom", new HashMap<>(eventGeneric));
            }
            vars.put("__event", ev);

            ScriptContext child = new ScriptContext(plugin, player, subjectId, phase, vars);
            try {
                ScriptEngine.runAllWithResult(child, actions);
            } catch (Exception ignored) {
            }
        }, 0L, interval);

        return ActionResult.ALLOW;
    }

}