package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;

public class EliteSpiderJockey extends CustomMob {

    public EliteSpiderJockey(RollAndDeathSMP plugin) {
        super(plugin, MobType.ELITE_SPIDER_JOCKEY, EntityType.SPIDER);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 30.0);
        setAttackDamage(entity, 6.0);
    }

    @Override
    public LivingEntity spawn(org.bukkit.Location location) {
        LivingEntity spider = super.spawn(location);
        
        Skeleton skeleton = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
        skeleton.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
        setMaxHealth(skeleton, 30.0);
        
        spider.addPassenger(skeleton);
        return spider;
    }
}
