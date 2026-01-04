package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class LivesAtLeastCondition implements Condition {

    private final int value;
    private final Object targetSpec;

    public static void register() {
        ConditionRegistrar.register("lives_at_least", LivesAtLeastCondition::new);
    }

    public LivesAtLeastCondition(Map<?, ?> spec) {
        Integer v = Resolvers.integer(null, spec, "value", "amount");
        this.value = v != null ? v : 1;
        this.targetSpec = spec.get("target");
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (ctx == null || ctx.plugin() == null || ctx.plugin().getLifeManager() == null) {
            return false;
        }

        if (!ctx.plugin().getLifeManager().isEnabled()) {
            return true;
        }

        List<Entity> targets = Resolvers.entities(ctx, targetSpec);
        if (targets.isEmpty()) {
            if (targetSpec == null && ctx.player() != null) {
                targets = List.of(ctx.player());
            } else {
                return false;
            }
        }

        for (Entity e : targets) {
            if (e instanceof Player p) {
                if (ctx.plugin().getLifeManager().getLives(p) < value) {
                    return false;
                }
            }
        }
        return true;
    }
}
