package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

final class HealAction {
    private HealAction() {}

    static void register() {
        ActionRegistrar.register("heal", HealAction::parse, "heal_entity", "heal_player", "set_health");
    }

    private static Action parse(Map<?, ?> raw) {
        Object targetSpec = raw.get("target");
        if (targetSpec == null) targetSpec = raw.get("entity");
        
        Object amountSpec = Resolvers.plain(raw, "amount", "health", "value");
        boolean set = "set_health".equalsIgnoreCase(String.valueOf(raw.get("type"))) || raw.containsKey("set");

        return ctx -> {
            List<Entity> targets = Resolvers.entities(ctx, targetSpec);
            if (targets.isEmpty()) {
                if (targetSpec == null && ctx.subject() instanceof LivingEntity) {
                    targets = List.of(ctx.subject());
                } else {
                    return ActionResult.ALLOW;
                }
            }

            Double amount = Resolvers.doubleVal(ctx, amountSpec);
            if (amount == null) return ActionResult.ALLOW;

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity e : targets) {
                    if (e instanceof LivingEntity le) {
                        double max = le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                        double current = le.getHealth();
                        double newVal;
                        
                        if (set) {
                            newVal = Math.max(0, Math.min(max, amount));
                        } else {
                            newVal = Math.max(0, Math.min(max, current + amount));
                        }
                        
                        le.setHealth(newVal);
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }
}
