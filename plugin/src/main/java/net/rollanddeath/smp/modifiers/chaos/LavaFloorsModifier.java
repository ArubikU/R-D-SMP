package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFromToEvent;

public class LavaFloorsModifier extends Modifier {

    public LavaFloorsModifier(RollAndDeathSMP plugin) {
        super(plugin, "Pisos de Lava", ModifierType.CHAOS, "La lava fluye tan rápido como el agua.");
    }

    @EventHandler
    public void onLiquidFlow(BlockFromToEvent event) {
        Block source = event.getBlock();
        if (source.getType() == Material.LAVA) {
            if (source.getBlockData() instanceof Levelled) {
                Levelled sourceData = (Levelled) source.getBlockData();
                int currentLevel = sourceData.getLevel();

                // Water flows up to level 7. Lava in overworld usually drops by 2.
                // To make it like water (fast and far), we drop by 1.
                if (currentLevel < 7) {
                    Block toBlock = event.getToBlock();
                    // Only accelerate if flowing into air or replaceable
                    if (toBlock.getType() == Material.AIR || !toBlock.getType().isSolid()) {
                        event.setCancelled(true);
                        toBlock.setType(Material.LAVA, false);
                        
                        if (toBlock.getBlockData() instanceof Levelled) {
                            Levelled newData = (Levelled) toBlock.getBlockData();
                            newData.setLevel(currentLevel + 1);
                            toBlock.setBlockData(newData);
                        }
                    }
                }
            }
        }
    }
}
