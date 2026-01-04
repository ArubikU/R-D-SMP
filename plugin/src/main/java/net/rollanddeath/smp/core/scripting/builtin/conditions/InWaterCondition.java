package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Map;

public class InWaterCondition implements Condition {

    private final boolean expected;
    private final boolean invert;
    private final Object targetSpec;

    public static void register() {
        ConditionRegistrar.register("in_water", InWaterCondition::new, "player_in_water", "player_is_in_water");
    }

    public InWaterCondition(Map<?, ?> spec) {
        this.expected = Resolvers.bool(null, spec.get("value")) != Boolean.FALSE; // default true
        this.invert = Resolvers.bool(null, spec.get("invert")) == Boolean.TRUE;
        this.targetSpec = spec.get("target");
    }

    @Override
    public boolean test(ScriptContext ctx) {
        List<Entity> targets = Resolvers.entities(ctx, targetSpec);
        if (targets.isEmpty()) {
            if (targetSpec == null && ctx.subject() != null) {
                targets = List.of(ctx.subject());
            } else {
                return false;
            }
        }

        boolean allMatch = true;
        for (Entity e : targets) {
            boolean in;
            try {
                in = e.isInWater();
            } catch (Exception ignored) {
                in = false;
            }
            if (in != expected) {
                allMatch = false;
                break;
            }
        }
        return invert ? !allMatch : allMatch;
    }
}
