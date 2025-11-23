package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class TheStalker extends CustomMob {

    public TheStalker(RollAndDeathSMP plugin) {
        super(plugin, MobType.THE_STALKER, EntityType.ENDERMAN);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 50.0);
        setAttackDamage(entity, 10.0);
        setMovementSpeed(entity, 0.4);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity().getScoreboardTags().contains(MobType.THE_STALKER.name())) {
            // Prevent teleporting when hit?
            // Enderman teleport logic is tricky to cancel via API without cancelling damage sometimes.
            // But we can try to cancel EntityTeleportEvent if we want.
        }
    }
    
    @EventHandler
    public void onTeleport(org.bukkit.event.entity.EntityTeleportEvent event) {
        if (event.getEntity().getScoreboardTags().contains(MobType.THE_STALKER.name())) {
            if (event.getEntity() instanceof Enderman) {
                // 50% chance to cancel teleport to make it more "stalker" like (chasing)
                if (Math.random() < 0.5) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
