package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Map;

public class HasStormCondition implements Condition {

    private final boolean expected;
    private final Object targetSpec;

    public static void register() {
        ConditionRegistrar.register("has_storm", HasStormCondition::new, "world_has_storm");
    }

    public HasStormCondition(Map<?, ?> spec) {
        this.expected = Resolvers.bool(null, spec.get("value")) != Boolean.FALSE; // default true
        this.targetSpec = spec.get("target");
    }

    @Override
    public boolean test(ScriptContext ctx) {
        List<Entity> targets = Resolvers.entities(ctx, targetSpec);
        if (targets.isEmpty()) {
            if (targetSpec == null && ctx.player() != null) {
                targets = List.of(ctx.player());
            } else {
                if (ctx.world() != null) {
                    return ctx.world().hasStorm() == expected;
                }
                return false;
            }
        }

        for (Entity e : targets) {
            if (e.getWorld().hasStorm() != expected) return false;
        }
        return true;
    }
}
