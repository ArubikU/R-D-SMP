package net.rollanddeath.smp.core.mobs;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.entity.LivingEntity;
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
        // Never replace mobs spawned by the plugin itself
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;

        // Slime/MagmaCube children inherit scoreboard tags from the parent.
        // If the parent was a custom mob, we MUST strip those tags here or the
        // split chain can be re-converted endlessly by the rotation system.
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) {
            LivingEntity entity = event.getEntity();
            if (entity.getScoreboardTags().contains("custom_mob")) {
                // Hook ScriptedMobs: ejecuta evento antes de limpiar tags
                try {
                    var scripted = plugin.getScriptedMobManager();
                    if (scripted != null) {
                        scripted.runtime().onSlimeSplitChild(entity);
                    }
                } catch (Exception ignored) {
                }

                entity.removeScoreboardTag("custom_mob");
                for (String id : plugin.getMobManager().getMobIds()) {
                    entity.removeScoreboardTag(id);
                }
                entity.customName(null);
                entity.setCustomNameVisible(false);
            }
            return;
        }

        DailyMobRotationManager rotationManager = plugin.getDailyMobRotationManager();
        String replacementId = rotationManager.getReplacement(event.getEntityType());

        if (replacementId != null) {
            double chance = rotationManager.getSpawnChance(replacementId);
            if (random.nextDouble() < chance) {
                event.setCancelled(true);
                plugin.getMobManager().spawnMob(replacementId, event.getLocation());
            }
        }
    }
}
