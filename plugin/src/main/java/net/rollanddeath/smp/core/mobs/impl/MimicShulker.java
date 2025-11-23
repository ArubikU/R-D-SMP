package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Shulker;

public class MimicShulker extends CustomMob {

    public MimicShulker(RollAndDeathSMP plugin) {
        super(plugin, MobType.MIMIC_SHULKER, EntityType.SHULKER);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 40.0);
        if (entity instanceof Shulker) {
            ((Shulker) entity).setColor(DyeColor.RED); // Red box to look like a chest? Or just distinct.
        }
    }
}
