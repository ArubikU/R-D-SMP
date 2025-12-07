package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class VoidWalker extends CustomMob {

    public VoidWalker(RollAndDeathSMP plugin) {
        super(plugin, MobType.VOID_WALKER, EntityType.WITHER_SKELETON);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 40.0);
        setAttackDamage(entity, 10.0);
        setMovementSpeed(entity, 0.38);
    }

    @Override
    protected void applyEquipment(LivingEntity entity) {
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = sword.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(true);
            sword.setItemMeta(meta);
        }
        entity.getEquipment().setItemInMainHand(sword);
        entity.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
        entity.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
        entity.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
        entity.getEquipment().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
    }
}
