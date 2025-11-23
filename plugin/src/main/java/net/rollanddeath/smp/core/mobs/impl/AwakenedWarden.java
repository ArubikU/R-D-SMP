package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class AwakenedWarden extends CustomMob {

    public AwakenedWarden(RollAndDeathSMP plugin) {
        super(plugin, MobType.AWAKENED_WARDEN, EntityType.WARDEN);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 1000.0);
        setAttackDamage(entity, 45.0);
        setMovementSpeed(entity, 0.4);
    }
}
