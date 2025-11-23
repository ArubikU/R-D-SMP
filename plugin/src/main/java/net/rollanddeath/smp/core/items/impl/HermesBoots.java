package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;

public class HermesBoots extends CustomItem {

    public HermesBoots(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.HERMES_BOOTS);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.LEATHER_BOOTS);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Botas bendecidas por Hermes", "Velocidad +30%");
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = super.getItemStack();
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        if (meta != null) {
            meta.setColor(Color.WHITE);
            meta.addAttributeModifier(Attribute.MOVEMENT_SPEED, 
                new AttributeModifier(new NamespacedKey(plugin, "hermes_boots_speed"), 0.3, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.FEET));
            item.setItemMeta(meta);
        }
        return item;
    }
}
