package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

public class DivineBlacksmithModifier extends Modifier {

    public DivineBlacksmithModifier(JavaPlugin plugin) {
        super(plugin, "Herrero Divino", ModifierType.BLESSING, "Yunques nunca se rompen.");
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() == InventoryType.ANVIL) {
            if (event.getInventory().getLocation() != null) {
                Block block = event.getInventory().getLocation().getBlock();
                if (block.getType() == Material.CHIPPED_ANVIL || block.getType() == Material.DAMAGED_ANVIL) {
                    block.setType(Material.ANVIL);
                }
            }
        }
    }
}
