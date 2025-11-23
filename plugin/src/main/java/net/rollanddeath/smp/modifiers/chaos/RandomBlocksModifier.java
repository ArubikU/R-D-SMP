package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class RandomBlocksModifier extends Modifier {

    private final Random random = new Random();
    private final Material[] materials = Material.values();

    public RandomBlocksModifier(RollAndDeathSMP plugin) {
        super(plugin, "Bloques Random", ModifierType.CHAOS, "Los drops de bloques están aleatorizados hoy.");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isDropItems()) {
            event.setDropItems(false);
            Material randomMaterial = materials[random.nextInt(materials.length)];
            while (!randomMaterial.isItem() || randomMaterial.isAir()) {
                randomMaterial = materials[random.nextInt(materials.length)];
            }
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(randomMaterial));
        }
    }
}
