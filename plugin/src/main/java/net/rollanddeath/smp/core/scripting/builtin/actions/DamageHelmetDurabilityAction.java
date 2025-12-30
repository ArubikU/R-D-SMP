package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

final class DamageHelmetDurabilityAction {
    private DamageHelmetDurabilityAction() {}

    static void register() {
        ActionRegistrar.register("damage_helmet_durability", DamageHelmetDurabilityAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Integer amount = Resolvers.integer(null, raw, "amount");
        int a = amount != null ? Math.max(1, amount) : 1;

        return ctx -> {
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            
            ActionUtils.runSync(ctx.plugin(), () -> {
                ItemStack item = player.getInventory().getItem(EquipmentSlot.HEAD);
                if (item == null || item.getType().isAir()) return;
                
                ItemMeta meta = item.getItemMeta();
                if (meta instanceof Damageable d) {
                    d.setDamage(d.getDamage() + a);
                    item.setItemMeta(meta);
                    if (d.getDamage() >= item.getType().getMaxDurability()) {
                        player.getInventory().setItem(EquipmentSlot.HEAD, null);
                        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ITEM_BREAK, 1f, 1f);
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
