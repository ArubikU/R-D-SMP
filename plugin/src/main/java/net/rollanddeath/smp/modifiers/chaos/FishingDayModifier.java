package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class FishingDayModifier extends Modifier {

    public FishingDayModifier(RollAndDeathSMP plugin) {
        super(plugin, "Día de Pesca", ModifierType.CHAOS, "Solo se puede comer pescado.");
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item.getType().isEdible()) {
            
            // Allow Notch Heart
            if (item.hasItemMeta()) {
                NamespacedKey key = new NamespacedKey(plugin, "custom_item_id");
                String id = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
                if ("NOTCH_HEART".equals(id)) {
                    return;
                }
            }

            if (item.getType() != Material.COOKED_COD && item.getType() != Material.COOKED_SALMON &&
                item.getType() != Material.COD && item.getType() != Material.SALMON &&
                item.getType() != Material.TROPICAL_FISH && item.getType() != Material.PUFFERFISH) {
                
                event.setCancelled(true);
                event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<red>¡Solo puedes comer pescado!"));
            }
        }
    }
}
