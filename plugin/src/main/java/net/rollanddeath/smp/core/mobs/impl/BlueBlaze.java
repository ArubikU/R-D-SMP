package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class BlueBlaze extends CustomMob {

    public BlueBlaze(RollAndDeathSMP plugin) {
        super(plugin, MobType.BLUE_BLAZE, EntityType.BLAZE);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 30.0);
        setAttackDamage(entity, 8.0);
        // Visuals would require packets or NMS to change fire color, 
        // or just assume it's a "Blue Blaze" by name and stats for now.
    }
}
