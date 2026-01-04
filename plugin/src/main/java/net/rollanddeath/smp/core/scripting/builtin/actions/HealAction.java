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
        ActionRegistrar.register("heal", HealAction::parse, "heal_entity", "heal_player", "set_health", "set_player_health");
    }

    private static Action parse(Map<?, ?> raw) {
        Object targetSpecRaw = raw.get("target");
        final Object targetSpec = targetSpecRaw != null ? targetSpecRaw : raw.get("entity");
        
        Object amountSpec = Resolvers.plain(raw, "amount", "health", "value");
        boolean set = "set_health".equalsIgnoreCase(String.valueOf(raw.get("type"))) || raw.containsKey("set");

        return ctx -> {
            List<Entity> resolvedTargets = Resolvers.entities(ctx, targetSpec);
            final List<Entity> targets;
            if (resolvedTargets.isEmpty()) {
                if (targetSpec == null && ctx.subject() instanceof LivingEntity) {
                    targets = List.of(ctx.subject());
                } else {
                    return ActionResult.ALLOW;
                }
            } else {
                targets = resolvedTargets;
            }

            Double amount = Resolvers.doubleVal(ctx, amountSpec);
            if (amount == null) return ActionResult.ALLOW;
            final double healAmount = amount;

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity e : targets) {
                    if (e instanceof LivingEntity le) {
                        double max = le.getAttribute(Attribute.MAX_HEALTH).getValue();
                        double current = le.getHealth();
                        double newVal;
                        
                        if (set) {
                            newVal = Math.max(0, Math.min(max, healAmount));
                        } else {
                            newVal = Math.max(0, Math.min(max, current + healAmount));
                        }
                        
                        le.setHealth(newVal);
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }
}
