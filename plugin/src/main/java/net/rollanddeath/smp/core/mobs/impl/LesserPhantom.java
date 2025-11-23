package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;

public class LesserPhantom extends CustomMob {

    public LesserPhantom(RollAndDeathSMP plugin) {
        super(plugin, MobType.LESSER_PHANTOM, EntityType.PHANTOM);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 10.0);
        setAttackDamage(entity, 3.0);
        if (entity instanceof Phantom) {
            ((Phantom) entity).setSize(1);
        }
    }
}
