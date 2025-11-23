package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class IronShortageModifier extends Modifier {

    public IronShortageModifier(JavaPlugin plugin) {
        super(plugin, "Escasez de Hierro", ModifierType.CURSE, "Golems hostiles. Ores de hierro reducidos.");
    }

    @EventHandler
    public void onGolemSpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.IRON_GOLEM) {
            IronGolem golem = (IronGolem) event.getEntity();
            // Make them hostile to nearest player if possible, or just aggressive AI
            // Since we can't easily set AI target without NMS or complex logic, 
            // we can try setting them as not player created (so they don't protect players)
            golem.setPlayerCreated(false);
            
            // Find nearest player to target immediately
            Player nearest = null;
            double distSq = Double.MAX_VALUE;
            for (Player p : golem.getWorld().getPlayers()) {
                if (p.getLocation().distanceSquared(golem.getLocation()) < 100 && p.getLocation().distanceSquared(golem.getLocation()) < distSq) {
                    distSq = p.getLocation().distanceSquared(golem.getLocation());
                    nearest = p;
                }
            }
            if (nearest != null) {
                golem.setTarget(nearest);
            }
        }
    }

    @EventHandler
    public void onIronBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.IRON_ORE || event.getBlock().getType() == Material.DEEPSLATE_IRON_ORE) {
            // 50% chance to drop nothing
            if (Math.random() < 0.5) {
                event.setDropItems(false);
                event.setExpToDrop(0);
                // Manually break block without drops
                event.getBlock().setType(Material.AIR); 
                // Cancel event so vanilla doesn't drop, but we already setDropItems false
                // Actually setDropItems(false) is enough for the drops, but we want the block to break.
            }
        }
    }
}
