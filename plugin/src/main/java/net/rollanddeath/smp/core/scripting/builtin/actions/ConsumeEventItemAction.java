package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

final class ConsumeEventItemAction {
    private ConsumeEventItemAction() {
    }

    static void register() {
        ActionRegistrar.register("consume_event_item", ConsumeEventItemAction::parse, "consume_item");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object amountSpec = Resolvers.plain(raw, "amount");
        return consume(amountSpec);
    }

    private static Action consume(Object amountSpec) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            Integer amount = Resolvers.integer(ctx, amountSpec);
            int consume = amount != null ? Math.max(1, amount) : 1;

            Object ev = ctx.nativeEvent();
            ActionUtils.runSync(plugin, () -> {
                try {
                    if (ev instanceof org.bukkit.event.player.PlayerInteractEvent pie) {
                        var hand = pie.getHand();
                        ItemStack item = pie.getItem();
                        if (item == null) return;

                        int next = item.getAmount() - consume;
                        if (next <= 0) {
                            item.setAmount(0);
                            if (hand == org.bukkit.inventory.EquipmentSlot.HAND) {
                                player.getInventory().setItemInMainHand(null);
                            } else if (hand == org.bukkit.inventory.EquipmentSlot.OFF_HAND) {
                                player.getInventory().setItemInOffHand(null);
                            }
                        } else {
                            item.setAmount(next);
                            if (hand == org.bukkit.inventory.EquipmentSlot.HAND) {
                                player.getInventory().setItemInMainHand(item);
                            } else if (hand == org.bukkit.inventory.EquipmentSlot.OFF_HAND) {
                                player.getInventory().setItemInOffHand(item);
                            }
                        }
                        return;
                    }

                    if (ev instanceof org.bukkit.event.player.PlayerItemConsumeEvent pce) {
                        ItemStack item = pce.getItem();
                        if (item == null) return;
                        int next = item.getAmount() - consume;
                        item.setAmount(Math.max(0, next));
                    }
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }
}
