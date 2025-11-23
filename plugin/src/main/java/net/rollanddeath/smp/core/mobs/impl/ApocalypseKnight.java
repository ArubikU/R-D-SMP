package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.inventory.ItemStack;

public class ApocalypseKnight extends CustomMob {

    public ApocalypseKnight(RollAndDeathSMP plugin) {
        super(plugin, MobType.APOCALYPSE_KNIGHT, EntityType.WITHER_SKELETON);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 80.0);
        setAttackDamage(entity, 12.0);
    }

    @Override
    protected void applyEquipment(LivingEntity entity) {
        if (entity.getEquipment() != null) {
            entity.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
            entity.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
            entity.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
            entity.getEquipment().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
            entity.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));
        }
    }

    @Override
    public LivingEntity spawn(org.bukkit.Location location) {
        LivingEntity knight = super.spawn(location);
        
        SkeletonHorse horse = (SkeletonHorse) location.getWorld().spawnEntity(location, EntityType.SKELETON_HORSE);
        horse.setTamed(true);
        setMaxHealth(horse, 50.0);
        
        horse.addPassenger(knight);
        return knight;
    }
}
