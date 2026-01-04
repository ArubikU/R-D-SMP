package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EnvironmentCondition implements Condition {

    private final World.Environment environment;
    private final Object targetSpec;

    public static void register() {
        ConditionRegistrar.register("environment_is", EnvironmentCondition::new, "world_environment_is");
    }

    public EnvironmentCondition(Map<?, ?> spec) {
        String val = Resolvers.string(null, spec, "value", "env");
        World.Environment env = null;
        if (val != null) {
            try {
                env = World.Environment.valueOf(val.trim().toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {}
        }
        this.environment = env;
        this.targetSpec = spec.get("target");
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (environment == null) return false;

        List<Entity> targets = Resolvers.entities(ctx, targetSpec);
        if (targets.isEmpty()) {
            if (targetSpec == null && ctx.player() != null) {
                targets = List.of(ctx.player());
            } else {
                if (ctx.world() != null) {
                    return ctx.world().getEnvironment() == environment;
                }
                return false;
            }
        }

        for (Entity e : targets) {
            if (e.getWorld().getEnvironment() != environment) return false;
        }
        return true;
    }
}
