package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WorldDestroyerPickaxe extends CustomItem {

    public WorldDestroyerPickaxe(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.WORLD_DESTROYER_PICKAXE);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.NETHERITE_PICKAXE);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Mina 3x3 bloques (Tunnel bore).");
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isItem(item)) return;

        Block block = event.getBlock();
        
        int glassCount = 0;
        boolean fragileGlassActive = plugin.getModifierManager().isActive("Cristal Frágil");

        // Check center block
        if (block.getType().name().contains("GLASS")) {
            glassCount++;
        }

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    Block relative = block.getRelative(x, y, z);
                    if (relative.getType() != Material.BEDROCK && relative.getType() != Material.AIR) {
                        if (relative.getType().name().contains("GLASS")) {
                            glassCount++;
                        }
                        relative.breakNaturally(item);
                    }
                }
            }
        }

        if (fragileGlassActive && glassCount > 0) {
            // Base power 2.0F + 0.5F per glass block. Max ~6.5F for 9 blocks.
            float power = 2.0F + (glassCount * 0.5F);
            block.getWorld().createExplosion(block.getLocation(), power, false, false);
        }
    }
}
