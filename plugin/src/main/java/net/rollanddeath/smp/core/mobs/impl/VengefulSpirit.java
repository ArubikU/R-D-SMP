package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vex;

public class VengefulSpirit extends CustomMob {

    public VengefulSpirit(RollAndDeathSMP plugin) {
        super(plugin, MobType.VENGEFUL_SPIRIT, EntityType.VEX);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 14.0);
        setAttackDamage(entity, 8.0);
        if (entity instanceof Vex) {
            ((Vex) entity).setCharging(true);
        }
    }
}
