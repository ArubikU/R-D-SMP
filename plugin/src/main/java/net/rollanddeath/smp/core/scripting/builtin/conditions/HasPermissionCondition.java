package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Map;

public class HasPermissionCondition implements Condition {

    private final String permission;
    private final boolean invert;
    private final Object targetSpec;

    public static void register() {
        ConditionRegistrar.register("has_permission", HasPermissionCondition::new, "player_has_permission");
    }

    public HasPermissionCondition(Map<?, ?> spec) {
        this.permission = Resolvers.string(null, spec, "permission", "perm");
        this.invert = Resolvers.bool(null, spec.get("invert")) == Boolean.TRUE;
        this.targetSpec = spec.get("target");
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (permission == null) return false;

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
            if (!e.hasPermission(permission)) {
                allMatch = false;
                break;
            }
        }
        return invert ? !allMatch : allMatch;
    }
}
