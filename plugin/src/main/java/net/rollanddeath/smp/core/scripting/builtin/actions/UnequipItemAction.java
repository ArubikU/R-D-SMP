package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.integration.PlaceholderUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

final class UnequipItemAction {
    private UnequipItemAction() {}

    static void register() {
        ActionRegistrar.register("unequip_item", UnequipItemAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String slotName = Resolvers.string(null, raw, "slot");
        EquipmentSlot slot = Resolvers.resolveEquipmentSlot(slotName);
        if (slot == null) return null;
        String message = Resolvers.string(null, raw, "message");
        String color = Resolvers.string(null, raw, "color");
        boolean dropIfFull = raw.get("drop_if_full") instanceof Boolean b ? b : true;

        return ctx -> {
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            
            ActionUtils.runSync(ctx.plugin(), () -> {
                ItemStack item = player.getInventory().getItem(slot);
                if (item == null || item.getType().isAir()) return;
                
                player.getInventory().setItem(slot, null);
                
                Map<Integer, ItemStack> left = player.getInventory().addItem(item);
                if (!left.isEmpty() && dropIfFull) {
                    for (ItemStack i : left.values()) {
                        player.getWorld().dropItemNaturally(player.getLocation(), i);
                    }
                }
                
                if (message != null && !message.isBlank()) {
                    NamedTextColor c = java.util.Optional.ofNullable(Resolvers.resolveColor(color)).orElse(NamedTextColor.RED);
                    String text = PlaceholderUtil.resolvePlaceholders(ctx.plugin(), player, message);
                    player.sendMessage(Component.text(text, c));
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
