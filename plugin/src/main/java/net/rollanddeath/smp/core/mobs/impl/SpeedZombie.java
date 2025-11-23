package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;

public class SpeedZombie extends CustomMob {

    public SpeedZombie(RollAndDeathSMP plugin) {
        super(plugin, MobType.SPEED_ZOMBIE, EntityType.ZOMBIE);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 15.0);
        setMovementSpeed(entity, 0.45); // Very fast
        if (entity instanceof Zombie) {
            ((Zombie) entity).setBaby(true); // Baby zombies are naturally faster too
        }
    }
}
