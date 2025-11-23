package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class ZombieInvasionModifier extends Modifier {

    public ZombieInvasionModifier(RollAndDeathSMP plugin) {
        super(plugin, "Invasión Zombie", ModifierType.CHAOS, "Solo spawnean Zombies, pero muchísimos.");
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            if (event.getEntity() instanceof Monster && event.getEntityType() != EntityType.ZOMBIE) {
                event.setCancelled(true);
                event.getLocation().getWorld().spawnEntity(event.getLocation(), EntityType.ZOMBIE);
            }
        }
    }
}
