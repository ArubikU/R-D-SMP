package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LeapOfFaithModifier extends Modifier {

    public LeapOfFaithModifier(JavaPlugin plugin) {
        super(plugin, "Salto de Fe", ModifierType.BLESSING, "No recibes daño de caída en césped.");
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Block block = player.getLocation().getBlock().getRelative(0, -1, 0);
            Material type = block.getType();
            
            if (isSoftBlock(type)) {
                event.setCancelled(true);
            }
        }
    }

    private boolean isSoftBlock(Material type) {
        return type == Material.GRASS_BLOCK || 
               type == Material.DIRT || 
               type == Material.COARSE_DIRT || 
               type == Material.PODZOL || 
               type == Material.MYCELIUM || 
               type == Material.MOSS_BLOCK || 
               type == Material.HAY_BLOCK ||
               type.name().contains("LEAVES");
    }
}
