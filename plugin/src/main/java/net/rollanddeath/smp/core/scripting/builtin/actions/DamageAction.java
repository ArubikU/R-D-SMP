package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

final class DamageAction {
    private DamageAction() {}

    static void register() {
        ActionRegistrar.register("damage", DamageAction::parse, "damage_entity", "damage_player", "kill", "kill_entity", "kill_player");
    }

    private static Action parse(Map<?, ?> raw) {
        Object targetSpec = raw.get("target");
        if (targetSpec == null) targetSpec = raw.get("entity");
        
        Object amountSpec = Resolvers.plain(raw, "amount", "damage", "value");
        boolean ignoreArmor = raw.get("ignore_armor") instanceof Boolean b ? b : false;
        
        // "kill" alias or "amount: kill"
        boolean isKill = "kill".equalsIgnoreCase(String.valueOf(raw.get("type"))) || "kill".equalsIgnoreCase(String.valueOf(amountSpec));

        final Object finalTargetSpec = targetSpec;
        final Object finalAmountSpec = amountSpec;
        final boolean finalIgnoreArmor = ignoreArmor;
        final boolean finalIsKill = isKill;

        return ctx -> {
            List<Entity> targets = Resolvers.entities(ctx, finalTargetSpec);
            if (targets.isEmpty()) {
                if (finalTargetSpec == null && ctx.subject() instanceof LivingEntity) {
                    targets = List.of(ctx.subject());
                } else {
                    return ActionResult.ALLOW;
                }
            }

            Double amount = null;
            if (!finalIsKill) {
                amount = Resolvers.doubleVal(ctx, finalAmountSpec);
            }

            final Double finalAmount = amount;
            final List<Entity> finalTargets = targets;

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity e : finalTargets) {
                    if (e instanceof LivingEntity le) {
                        if (finalIsKill) {
                            le.setHealth(0);
                        } else if (finalAmount != null) {
                            if (finalIgnoreArmor) {
                                double newHealth = Math.max(0, le.getHealth() - finalAmount);
                                le.setHealth(newHealth);
                                le.damage(0.0001); // Trigger damage animation/event
                            } else {
                                le.damage(finalAmount);
                            }
                        }
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }
}
