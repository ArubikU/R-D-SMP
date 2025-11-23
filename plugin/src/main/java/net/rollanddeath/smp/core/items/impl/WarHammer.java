package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class WarHammer extends CustomItem {

    public WarHammer(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.WAR_HAMMER);
    }

    @Override
    protected ItemStack createBaseItem() {
        ItemStack item = new ItemStack(Material.IRON_AXE);
        ItemMeta meta = item.getItemMeta();
        
        // Slow attack speed
        AttributeModifier speed = new AttributeModifier(
            UUID.randomUUID(), 
            "generic.attack_speed", 
            -3.0, 
            AttributeModifier.Operation.ADD_NUMBER, 
            EquipmentSlot.HAND
        );
        meta.addAttributeModifier(Attribute.ATTACK_SPEED, speed);
        
        // High damage
        AttributeModifier damage = new AttributeModifier(
            UUID.randomUUID(), 
            "generic.attack_damage", 
            10.0, 
            AttributeModifier.Operation.ADD_NUMBER, 
            EquipmentSlot.HAND
        );
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, damage);
        
        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected List<String> getLore() {
        return List.of("Daño de área masivo, recarga muy lenta.");
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (!isItem(item)) return;

        // Area damage
        for (Entity entity : player.getNearbyEntities(3, 3, 3)) {
            if (entity instanceof LivingEntity && entity != player && entity != event.getEntity()) {
                ((LivingEntity) entity).damage(event.getDamage() * 0.5, player);
            }
        }
        
        // Play sound
        player.getWorld().playSound(player.getLocation(), "entity.iron_golem.attack", 1, 0.5f);
    }
}
