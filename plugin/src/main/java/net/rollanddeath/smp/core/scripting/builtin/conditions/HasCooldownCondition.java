package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class HasCooldownCondition implements Condition {

    private final Material material;
    private final boolean expected;
    private final boolean invert;
    private final Object targetSpec;

    public static void register() {
        ConditionRegistrar.register("has_cooldown", HasCooldownCondition::new, "player_has_cooldown");
    }

    public HasCooldownCondition(Map<?, ?> spec) {
        this.material = Resolvers.material(null, spec, "material", "mat");
        this.expected = Resolvers.bool(null, spec.get("value")) != Boolean.FALSE; // default true
        this.invert = Resolvers.bool(null, spec.get("invert")) == Boolean.TRUE;
        this.targetSpec = spec.get("target");
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (material == null) return false;

        List<Entity> targets = Resolvers.entities(ctx, targetSpec);
        if (targets.isEmpty()) {
            if (targetSpec == null && ctx.player() != null) {
                targets = List.of(ctx.player());
            } else {
                return false;
            }
        }

        boolean allMatch = true;
        for (Entity e : targets) {
            if (!(e instanceof Player p)) {
                allMatch = false;
                break;
            }
            boolean has;
            try {
                has = p.hasCooldown(material);
            } catch (Exception ignored) {
                has = false;
            }
            if (has != expected) {
                allMatch = false;
                break;
            }
        }
        return invert ? !allMatch : allMatch;
    }
}
