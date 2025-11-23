package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ArmoredWings extends CustomItem {

    public ArmoredWings(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.ARMORED_WINGS);
    }

    @Override
    protected ItemStack createBaseItem() {
        ItemStack item = new ItemStack(Material.ELYTRA);
        ItemMeta meta = item.getItemMeta();
        
        AttributeModifier armor = new AttributeModifier(
            new NamespacedKey(plugin, "armored_wings_armor"), 
            6.0, 
            AttributeModifier.Operation.ADD_NUMBER, 
            EquipmentSlotGroup.CHEST
        );
        meta.addAttributeModifier(Attribute.ARMOR, armor);
        
        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected List<String> getLore() {
        return List.of("Elytras que dan protección como pechera de hierro.");
    }
}
