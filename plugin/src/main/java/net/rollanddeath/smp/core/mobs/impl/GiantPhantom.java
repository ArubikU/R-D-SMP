package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;

public class GiantPhantom extends CustomMob {

    public GiantPhantom(RollAndDeathSMP plugin) {
        super(plugin, MobType.GIANT_PHANTOM, EntityType.PHANTOM);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 60.0);
        setAttackDamage(entity, 12.0);
        if (entity instanceof Phantom) {
            ((Phantom) entity).setSize(10);
        }
    }
}
