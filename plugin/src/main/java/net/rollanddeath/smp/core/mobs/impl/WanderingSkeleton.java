package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class WanderingSkeleton extends CustomMob {

    public WanderingSkeleton(RollAndDeathSMP plugin) {
        super(plugin, MobType.WANDERING_SKELETON, EntityType.STRAY);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 20.0);
        setMovementSpeed(entity, 0.2);
    }
}
