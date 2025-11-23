package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class AlphaDragon extends CustomMob {

    public AlphaDragon(RollAndDeathSMP plugin) {
        super(plugin, MobType.ALPHA_DRAGON, EntityType.ENDER_DRAGON);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 500.0);
        // Ender Dragon damage is handled differently, but we can try setting generic attack damage
        setAttackDamage(entity, 20.0);
    }
}
