package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Map;

public class SkyLightCondition implements Condition {

    private final int value;
    private final Object targetSpec;

    public static void register() {
        ConditionRegistrar.register("sky_light_at_least", SkyLightCondition::new, "player_sky_light_at_least");
    }

    public SkyLightCondition(Map<?, ?> spec) {
        Integer v = Resolvers.integer(null, spec, "value", "level");
        this.value = v != null ? Math.max(0, Math.min(15, v)) : 0;
        this.targetSpec = spec.get("target");
    }

    @Override
    public boolean test(ScriptContext ctx) {
        List<Entity> targets = Resolvers.entities(ctx, targetSpec);
        if (targets.isEmpty()) {
            if (targetSpec == null && ctx.player() != null) {
                targets = List.of(ctx.player());
            } else {
                return false;
            }
        }

        for (Entity e : targets) {
            try {
                if (e.getLocation().getBlock().getLightFromSky() < value) {
                    return false;
                }
            } catch (Exception ignored) {
                return false;
            }
        }
        return true;
    }
}
