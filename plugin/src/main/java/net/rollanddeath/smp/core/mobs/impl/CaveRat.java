package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class CaveRat extends CustomMob {

    public CaveRat(RollAndDeathSMP plugin) {
        super(plugin, MobType.CAVE_RAT, EntityType.SILVERFISH);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 10.0);
        setAttackDamage(entity, 2.0);
        setMovementSpeed(entity, 0.3);
    }
}
