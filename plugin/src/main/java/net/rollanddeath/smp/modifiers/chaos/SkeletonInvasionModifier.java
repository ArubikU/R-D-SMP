package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class SkeletonInvasionModifier extends Modifier {

    public SkeletonInvasionModifier(RollAndDeathSMP plugin) {
        super(plugin, "Invasión Esqueleto", ModifierType.CHAOS, "Solo spawnean Esqueletos.");
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            if (event.getEntity() instanceof Monster && event.getEntityType() != EntityType.SKELETON) {
                event.setCancelled(true);
                event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.SKELETON);
            }
        }
    }
}
