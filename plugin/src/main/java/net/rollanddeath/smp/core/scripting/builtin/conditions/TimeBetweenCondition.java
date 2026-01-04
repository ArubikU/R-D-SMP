package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Map;

public class TimeBetweenCondition implements Condition {

    private final Integer min;
    private final Integer max;
    private final boolean inclusive;
    private final Object targetSpec;

    public static void register() {
        ConditionRegistrar.register("time_between", TimeBetweenCondition::new, "world_time_between");
    }

    public TimeBetweenCondition(Map<?, ?> spec) {
        this.min = Resolvers.integer(null, spec, "min");
        this.max = Resolvers.integer(null, spec, "max");
        this.inclusive = Resolvers.bool(null, spec.get("inclusive")) != Boolean.FALSE; // default true
        this.targetSpec = spec.get("target");
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (min == null || max == null) return false;
        int a = ((min % 24000) + 24000) % 24000;
        int b = ((max % 24000) + 24000) % 24000;

        List<Entity> targets = Resolvers.entities(ctx, targetSpec);
        if (targets.isEmpty()) {
            // Fallback to world of context if no target specified?
            // Original logic used targetSpec or failed if empty?
            // Original logic: resolveTargets(ctx, targetSpec). If empty return false.
            // resolveTargets defaulted to ctx.player() if targetSpec null.
            if (targetSpec == null && ctx.player() != null) {
                targets = List.of(ctx.player());
            } else {
                // Try to get world from context directly?
                if (ctx.world() != null) {
                    return checkWorld(ctx.world(), a, b);
                }
                return false;
            }
        }

        for (Entity e : targets) {
            if (!checkWorld(e.getWorld(), a, b)) return false;
        }
        return true;
    }

    private boolean checkWorld(World w, int a, int b) {
        if (w == null) return false;
        long t0 = w.getTime();
        int t = (int) (t0 % 24000);
        if (a <= b) {
            return inclusive ? (t >= a && t <= b) : (t > a && t < b);
        } else {
            return inclusive ? (t >= a || t <= b) : (t > a || t < b);
        }
    }
}
