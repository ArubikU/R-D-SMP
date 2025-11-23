package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class MadEvoker extends CustomMob {

    public MadEvoker(RollAndDeathSMP plugin) {
        super(plugin, MobType.MAD_EVOKER, EntityType.EVOKER);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 60.0);
        setMovementSpeed(entity, 0.35);
    }
}
