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

import java.util.Collection;
import java.util.Comparator;

public class FortuneTouchModifier extends Modifier {

    public FortuneTouchModifier(JavaPlugin plugin) {
        super(plugin, "Toque de Fortuna", ModifierType.BLESSING, "Ores siempre dan el máximo drop posible.");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        String typeName = block.getType().name();
        
        if (typeName.endsWith("_ORE") || typeName.equals("ANCIENT_DEBRIS")) {
            // Skip if Silk Touch
            if (event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
                return;
            }

            ItemStack tool = new ItemStack(Material.DIAMOND_PICKAXE);
            tool.addEnchantment(Enchantment.FORTUNE, 3);

            // Try 10 times to get the best drop
            Collection<ItemStack> bestDrops = null;
            int maxCount = -1;

            for (int i = 0; i < 10; i++) {
                Collection<ItemStack> drops = block.getDrops(tool);
                int count = drops.stream().mapToInt(ItemStack::getAmount).sum();
                if (count > maxCount) {
                    maxCount = count;
                    bestDrops = drops;
                }
            }

            if (bestDrops != null) {
                event.setDropItems(false);
                for (ItemStack drop : bestDrops) {
                    block.getWorld().dropItemNaturally(block.getLocation(), drop);
                }
            }
        }
    }
}
