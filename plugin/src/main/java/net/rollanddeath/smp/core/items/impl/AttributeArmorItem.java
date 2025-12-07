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
import java.util.Locale;

public class AttributeArmorItem extends CustomItem {

    private final Material material;
    private final EquipmentSlotGroup slotGroup;
    private final double armorBonus;
    private final double toughnessBonus;
    private final double knockbackResistBonus;
    private final List<String> lore;

    public AttributeArmorItem(
            RollAndDeathSMP plugin,
            CustomItemType type,
            Material material,
            EquipmentSlotGroup slotGroup,
            double armorBonus,
            double toughnessBonus,
            double knockbackResistBonus,
            List<String> lore
    ) {
        super(plugin, type);
        this.material = material;
        this.slotGroup = slotGroup;
        this.armorBonus = armorBonus;
        this.toughnessBonus = toughnessBonus;
        this.knockbackResistBonus = knockbackResistBonus;
        this.lore = lore;
    }

    @Override
    protected ItemStack createBaseItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (armorBonus != 0.0) {
                meta.addAttributeModifier(
                        Attribute.ARMOR,
                        new AttributeModifier(new NamespacedKey(plugin, type.name().toLowerCase(Locale.ROOT) + "_armor"),
                                armorBonus,
                                AttributeModifier.Operation.ADD_NUMBER,
                                slotGroup)
                );
            }
            if (toughnessBonus != 0.0) {
                meta.addAttributeModifier(
                        Attribute.ARMOR_TOUGHNESS,
                        new AttributeModifier(new NamespacedKey(plugin, type.name().toLowerCase(Locale.ROOT) + "_toughness"),
                                toughnessBonus,
                                AttributeModifier.Operation.ADD_NUMBER,
                                slotGroup)
                );
            }
            if (knockbackResistBonus != 0.0) {
                meta.addAttributeModifier(
                        Attribute.KNOCKBACK_RESISTANCE,
                        new AttributeModifier(new NamespacedKey(plugin, type.name().toLowerCase(Locale.ROOT) + "_kb"),
                                knockbackResistBonus,
                                AttributeModifier.Operation.ADD_NUMBER,
                                slotGroup)
                );
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    protected List<String> getLore() {
        return lore;
    }
}
