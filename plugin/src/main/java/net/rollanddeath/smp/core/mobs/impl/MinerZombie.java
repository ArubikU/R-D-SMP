package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class MinerZombie extends CustomMob {

    public MinerZombie(RollAndDeathSMP plugin) {
        super(plugin, MobType.MINER_ZOMBIE, EntityType.ZOMBIE);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 25.0);
    }

    @Override
    protected void applyEquipment(LivingEntity entity) {
        if (entity.getEquipment() != null) {
            entity.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
            entity.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_PICKAXE));
        }
    }
}
