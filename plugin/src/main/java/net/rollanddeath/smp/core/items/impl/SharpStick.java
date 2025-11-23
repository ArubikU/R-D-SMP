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

public class SharpStick extends CustomItem {

    public SharpStick(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.SHARP_STICK);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.STICK);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Un palo muy afilado", "Daño: +4");
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = super.getItemStack();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, 
                new AttributeModifier(new NamespacedKey(plugin, "sharp_stick_damage"), 4.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND));
            item.setItemMeta(meta);
        }
        return item;
    }
}
