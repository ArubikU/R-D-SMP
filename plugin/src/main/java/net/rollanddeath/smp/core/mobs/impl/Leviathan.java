package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class Leviathan extends CustomMob {

    public Leviathan(RollAndDeathSMP plugin) {
        super(plugin, MobType.LEVIATHAN, EntityType.ELDER_GUARDIAN);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 200.0);
        setAttackDamage(entity, 15.0);
    }
}
