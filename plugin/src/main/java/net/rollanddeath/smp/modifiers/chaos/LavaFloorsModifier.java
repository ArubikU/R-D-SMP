package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
            // Accelerate flow by instantly setting the target block to lava
            // This is very chaotic and dangerous
            Block toBlock = event.getToBlock();
            if (toBlock.getType() == Material.AIR || !toBlock.getType().isSolid()) {
                event.setCancelled(true);
                toBlock.setType(Material.LAVA, true);
            }
        }
    }
}
