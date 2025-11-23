package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;

public class MagmaSlime extends CustomMob {

    public MagmaSlime(RollAndDeathSMP plugin) {
        super(plugin, MobType.MAGMA_SLIME, EntityType.MAGMA_CUBE);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 20.0);
        if (entity instanceof MagmaCube) {
            ((MagmaCube) entity).setSize(3);
        }
    }
}
