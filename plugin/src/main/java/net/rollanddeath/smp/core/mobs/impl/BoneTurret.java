package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class BoneTurret extends CustomMob {

    public BoneTurret(RollAndDeathSMP plugin) {
        super(plugin, MobType.BONE_TURRET, EntityType.SKELETON);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 40.0);
        setMovementSpeed(entity, 0.0); // Cannot move
        if (entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE) != null) {
            entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1.0); // Cannot be pushed
        }
    }

    @Override
    protected void applyEquipment(LivingEntity entity) {
        if (entity.getEquipment() != null) {
            entity.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
            entity.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
        }
    }
}
