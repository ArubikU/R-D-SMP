package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

final class MultiplyEventDamageAction {
    private MultiplyEventDamageAction() {
    }

    static void register() {
        ActionRegistrar.register("multiply_event_damage", MultiplyEventDamageAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object multiplierSpec = Resolvers.plain(raw, "multiplier");
        if (multiplierSpec == null) return null;
        return ctx -> {
            Double multiplier = Resolvers.doubleVal(ctx, multiplierSpec);
            if (multiplier == null) return ActionResult.ALLOW;
            double mult = multiplier;

            Object ev = ctx.nativeEvent();
            if (ev instanceof EntityDamageEvent dmg) {
                dmg.setDamage(Math.max(0.0, dmg.getDamage() * mult));
                return ActionResult.ALLOW;
            }
            if (ev instanceof PlayerItemDamageEvent itemDmg) {
                itemDmg.setDamage(Math.max(0, (int) Math.round(itemDmg.getDamage() * mult)));
                return ActionResult.ALLOW;
            }
            if (ev instanceof EntityDamageByEntityEvent byEntity) {
                byEntity.setDamage(Math.max(0.0, byEntity.getDamage() * mult));
                return ActionResult.ALLOW;
            }
            return ActionResult.ALLOW;
        };
    }
}