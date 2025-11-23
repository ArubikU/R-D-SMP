package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class TheReaper extends CustomMob {

    public TheReaper(RollAndDeathSMP plugin) {
        super(plugin, MobType.THE_REAPER, EntityType.WITHER_SKELETON);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 100.0);
        setAttackDamage(entity, 18.0);
        setMovementSpeed(entity, 0.3);
    }

    @Override
    protected void applyEquipment(LivingEntity entity) {
        if (entity.getEquipment() != null) {
            entity.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_HOE));
            entity.getEquipment().setHelmet(new ItemStack(Material.WITHER_SKELETON_SKULL));
        }
    }
}
