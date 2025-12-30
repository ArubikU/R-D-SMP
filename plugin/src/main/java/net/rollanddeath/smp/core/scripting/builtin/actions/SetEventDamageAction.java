package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;

final class SetEventDamageAction {
    private SetEventDamageAction() {
    }

    static void register() {
        ActionRegistrar.register("set_event_damage", SetEventDamageAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object valueSpec = Resolvers.plain(raw, "value");
        if (valueSpec == null) return null;
        return ctx -> {
            Double value = Resolvers.doubleVal(ctx, valueSpec);
            if (value == null) return ActionResult.ALLOW;

            Object ev = ctx.nativeEvent();
            if (ev instanceof EntityDamageEvent dmg) {
                dmg.setDamage(Math.max(0.0, value));
                return ActionResult.ALLOW;
            }
            if (ev instanceof PlayerItemDamageEvent itemDmg) {
                itemDmg.setDamage(Math.max(0, (int) Math.round(value)));
                return ActionResult.ALLOW;
            }
            if (ev instanceof EntityDamageByEntityEvent byEntity) {
                byEntity.setDamage(Math.max(0.0, value));
                return ActionResult.ALLOW;
            }
            return ActionResult.ALLOW;
        };
    }
}