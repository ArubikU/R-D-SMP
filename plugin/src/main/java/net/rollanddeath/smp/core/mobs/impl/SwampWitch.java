package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class SwampWitch extends CustomMob {

    public SwampWitch(RollAndDeathSMP plugin) {
        super(plugin, MobType.SWAMP_WITCH, EntityType.WITCH);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 40.0);
        setMovementSpeed(entity, 0.3);
    }
}
