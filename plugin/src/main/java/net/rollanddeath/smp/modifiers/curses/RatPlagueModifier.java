package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

public class RatPlagueModifier extends Modifier {

    private final Random random = new Random();
    private final Set<Material> stoneTypes = EnumSet.of(
            Material.STONE,
            Material.COBBLESTONE,
            Material.DEEPSLATE,
            Material.COBBLED_DEEPSLATE,
            Material.ANDESITE,
            Material.DIORITE,
            Material.GRANITE,
            Material.TUFF
    );

    public RatPlagueModifier(JavaPlugin plugin) {
        super(plugin, "Plaga de Ratas", ModifierType.CURSE, "5% de probabilidad de que aparezcan silverfish al picar piedra.");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (stoneTypes.contains(block.getType())) {
            if (random.nextDouble() < 0.05) { // 5% chance
                block.getWorld().spawnEntity(block.getLocation().add(0.5, 0, 0.5), EntityType.SILVERFISH);
            }
        }
    }
}
