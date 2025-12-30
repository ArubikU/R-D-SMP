package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

final class BowRefundConsumableAction {
    private BowRefundConsumableAction() {}

    static void register() {
        ActionRegistrar.register("bow_refund_consumable", BowRefundConsumableAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        return ctx -> {
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            EntityShootBowEvent bow = ctx.nativeEvent(EntityShootBowEvent.class);
            if (bow == null) return ActionResult.ALLOW;

            ActionUtils.runSync(ctx.plugin(), () -> {
                try {
                    bow.setConsumeItem(false);
                } catch (Exception ignored) {
                }

                try {
                    ItemStack shot = bow.getConsumable();
                    ItemStack refund;
                    if (shot != null) {
                        refund = shot.clone();
                        refund.setAmount(1);
                    } else {
                        refund = new ItemStack(Material.ARROW, 1);
                    }
                    player.getInventory().addItem(refund);
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
