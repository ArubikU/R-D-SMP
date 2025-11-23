package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class FragileGroundModifier extends Modifier {

    private final Random random = new Random();

    public FragileGroundModifier(JavaPlugin plugin) {
        super(plugin, "Suelo Frágil", ModifierType.CURSE, "Stone/Cobble tiene chance de convertirse en Gravel al pisar.");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && 
            event.getFrom().getBlockY() == event.getTo().getBlockY() && 
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Block block = event.getTo().getBlock().getRelative(BlockFace.DOWN);
        if (block.getType() == Material.STONE || block.getType() == Material.COBBLESTONE) {
            if (random.nextInt(100) < 5) { // 5% chance
                block.setType(Material.GRAVEL);
            }
        }
    }
}
