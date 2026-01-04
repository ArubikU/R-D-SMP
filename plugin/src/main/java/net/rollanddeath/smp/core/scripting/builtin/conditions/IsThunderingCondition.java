package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Map;

public class IsThunderingCondition implements Condition {

    private final boolean expected;
    private final Object targetSpec;

    public static void register() {
        ConditionRegistrar.register("is_thundering", IsThunderingCondition::new, "world_is_thundering");
    }

    public IsThunderingCondition(Map<?, ?> spec) {
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
                    return ctx.world().isThundering() == expected;
                }
                return false;
            }
        }

        for (Entity e : targets) {
            if (e.getWorld().isThundering() != expected) return false;
        }
        return true;
    }
}
