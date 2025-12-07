package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class Banshee extends CustomMob {

    public Banshee(RollAndDeathSMP plugin) {
        super(plugin, MobType.BANSHEE, EntityType.VEX);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 30.0);
        setAttackDamage(entity, 8.0);
        setMovementSpeed(entity, 0.55);
    }
}
