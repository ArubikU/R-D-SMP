package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class ArmoredSkeleton extends CustomMob {

    public ArmoredSkeleton(RollAndDeathSMP plugin) {
        super(plugin, MobType.ARMORED_SKELETON, EntityType.SKELETON);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 30.0);
    }

    @Override
    protected void applyEquipment(LivingEntity entity) {
        if (entity.getEquipment() != null) {
            entity.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
            entity.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            entity.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            entity.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
            entity.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
        }
    }
}
