package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;

public class SlimeKing extends CustomMob {

    public SlimeKing(RollAndDeathSMP plugin) {
        super(plugin, MobType.SLIME_KING, EntityType.SLIME);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 100.0);
        if (entity instanceof Slime) {
            ((Slime) entity).setSize(10);
        }
    }
}
