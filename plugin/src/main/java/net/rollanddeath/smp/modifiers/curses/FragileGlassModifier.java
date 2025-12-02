package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class FragileGlassModifier extends Modifier {

    public FragileGlassModifier(JavaPlugin plugin) {
        super(plugin, "Cristal Frágil", ModifierType.CURSE, "Romper cristal causa explosión pequeña.");
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        if (type.name().contains("GLASS")) {
            // If using World Destroyer Pickaxe, let the item handle the explosion logic
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            if (item.hasItemMeta()) {
                NamespacedKey key = new NamespacedKey(plugin, "custom_item_id");
                String id = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
                if ("WORLD_DESTROYER_PICKAXE".equals(id)) {
                    return;
                }
            }

            event.getBlock().getWorld().createExplosion(event.getBlock().getLocation(), 2.0F, false, false);
        }
    }
}
