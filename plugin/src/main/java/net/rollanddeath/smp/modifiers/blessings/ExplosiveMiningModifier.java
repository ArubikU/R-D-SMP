package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class ExplosiveMiningModifier extends Modifier {

    private final Random random = new Random();

    public ExplosiveMiningModifier(JavaPlugin plugin) {
        super(plugin, "Minería Explosiva", ModifierType.BLESSING, "Probabilidad de encontrar TNT o Diamantes al minar.");
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        if (isMineable(block.getType())) {
            if (random.nextDouble() < 0.01) { // 1% chance
                if (random.nextBoolean()) {
                    block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.TNT));
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>¡Has encontrado TNT!"));
                } else {
                    block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.DIAMOND));
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<aqua>¡Has encontrado un Diamante!"));
                }
            }
        }
    }

    private boolean isMineable(Material type) {
        return type.name().endsWith("STONE") || 
               type.name().endsWith("ORE") || 
               type == Material.DEEPSLATE || 
               type == Material.NETHERRACK ||
               type == Material.TUFF;
    }
}
