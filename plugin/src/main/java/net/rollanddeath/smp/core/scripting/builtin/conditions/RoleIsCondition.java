package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.roles.RoleType;
import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RoleIsCondition implements Condition {

    private final RoleType required;
    private final Object targetSpec;

    public static void register() {
        ConditionRegistrar.register("role_is", RoleIsCondition::new);
    }

    public RoleIsCondition(Map<?, ?> spec) {
        String val = Resolvers.string(null, spec, "value", "role");
        RoleType r = null;
        if (val != null) {
            try {
                r = RoleType.valueOf(val.trim().toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {}
        }
        this.required = r;
        this.targetSpec = spec.get("target");
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (required == null || ctx.plugin().getRoleManager() == null) return false;

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
                if (!Objects.equals(ctx.plugin().getRoleManager().getPlayerRole(p), required)) {
                    return false;
                }
            }
        }
        return true;
    }
}
