package net.rollanddeath.smp.core.mobs;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Random;

public class MobSpawnListener implements Listener {

    private final RollAndDeathSMP plugin;
    private final Random random = new Random();

    public MobSpawnListener(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
            return;
        }

        DailyMobRotationManager rotationManager = plugin.getDailyMobRotationManager();
        MobType replacement = rotationManager.getReplacement(event.getEntityType());

        if (replacement != null) {
            double chance = rotationManager.getSpawnChance(replacement);
            if (random.nextDouble() < chance) {
                event.setCancelled(true);
                plugin.getMobManager().spawnMob(replacement, event.getLocation());
            }
        }
    }
}
