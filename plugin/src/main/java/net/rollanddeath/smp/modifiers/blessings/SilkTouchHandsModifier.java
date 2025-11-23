package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SilkTouchHandsModifier extends Modifier {

    public SilkTouchHandsModifier(JavaPlugin plugin) {
        super(plugin, "Manos de Seda", ModifierType.BLESSING, "Toque de seda automático en todo.");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        
        // Skip if player already has Silk Touch
        ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
        if (hand.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return;
        }

        // Create a dummy tool with Silk Touch (Diamond Pickaxe to ensure drops)
        ItemStack dummyTool = new ItemStack(Material.DIAMOND_PICKAXE);
        dummyTool.addEnchantment(Enchantment.SILK_TOUCH, 1);

        // Get drops with Silk Touch
        event.setDropItems(false);
        for (ItemStack drop : block.getDrops(dummyTool)) {
            block.getWorld().dropItemNaturally(block.getLocation(), drop);
        }
    }
}
