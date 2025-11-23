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
        // Simple 3x3 logic based on player facing is complex.
        // Let's just break 3x3 around the block relative to the face hit?
        // We don't have the face hit in BlockBreakEvent.
        // We can approximate or just break 3x3x1 cube centered on block.
        
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    Block relative = block.getRelative(x, y, z);
                    if (relative.getType() != Material.BEDROCK && relative.getType() != Material.AIR) {
                        relative.breakNaturally(item);
                    }
                }
            }
        }
    }
}
