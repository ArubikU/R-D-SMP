package net.rollanddeath.smp.core.game;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Random;

public class EndBattleListener implements Listener {

    private final RollAndDeathSMP plugin;
    private final Random random = new Random();

    public EndBattleListener(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getLocation().getWorld().getEnvironment() != World.Environment.THE_END) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

        // Replace Enderman with Void Walker or Stalker rarely
        if (event.getEntityType() == EntityType.ENDERMAN) {
            double roll = random.nextDouble();
            if (roll < 0.05) { // 5% chance
                event.setCancelled(true);
                plugin.getMobManager().spawnMob("void_walker", event.getLocation());
            } else if (roll < 0.10) { // Another 5%
                event.setCancelled(true);
                plugin.getMobManager().spawnMob("the_stalker", event.getLocation());
            }
        }
    }

    @EventHandler
    public void onDragonDeath(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.ENDER_DRAGON) {
            // Spawn Alpha Dragon as a "ghost" or second phase sometimes?
            // Or just drop special loot.
            // For now, let's just ensure the Alpha Dragon rule is respected if it spawns naturally via rotation.
            // But user asked for "improvements to end battle".
            
            // Let's spawn 3 Void Walkers on Dragon death as guards of the egg
            for (int i = 0; i < 3; i++) {
                plugin.getMobManager().spawnMob("void_walker", event.getEntity().getLocation().add(random.nextInt(10)-5, 0, random.nextInt(10)-5));
            }
        }
    }
}
