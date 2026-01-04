package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

final class ReflectDamageIfBlockingAction {
    private ReflectDamageIfBlockingAction() {}

    static void register() {
        ActionRegistrar.register("reflect_damage_if_blocking", ReflectDamageIfBlockingAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Double multiplier = Resolvers.doubleVal(null, raw, "multiplier", "factor");
        Boolean resetTicks = Resolvers.bool(null, raw, "reset_no_damage_ticks", "reset_ticks");

        return ctx -> {
            Object ev = ctx.nativeEvent();
            if (!(ev instanceof EntityDamageByEntityEvent event)) return ActionResult.ALLOW;

            if (!(event.getEntity() instanceof Player player)) return ActionResult.ALLOW;
            
            if (!player.isBlocking()) return ActionResult.ALLOW;

            double dmg = event.getFinalDamage();
            double mult = multiplier != null ? multiplier : 1.0;
            double reflectDmg = dmg * mult;

            Entity attacker = event.getDamager();
            if (attacker instanceof LivingEntity livingAttacker) {
                ActionUtils.runSync(ctx.plugin(), () -> {
                    livingAttacker.damage(reflectDmg, player);
                    if (resetTicks != null && resetTicks) {
                        livingAttacker.setNoDamageTicks(0);
                    }
                });
            }

            return ActionResult.ALLOW;
        };
    }
}
