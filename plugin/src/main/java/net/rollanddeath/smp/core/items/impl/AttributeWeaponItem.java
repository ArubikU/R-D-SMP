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

import com.google.common.collect.Multimap;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AttributeWeaponItem extends CustomItem {

    private final Material material;
    private final double damageBonus;
    private final double speedBonus;
    private final double knockbackResistBonus;
    private final List<String> lore;

    public AttributeWeaponItem(
            RollAndDeathSMP plugin,
            CustomItemType type,
            Material material,
            double damageBonus,
            double speedBonus,
            double knockbackResistBonus,
            List<String> lore
    ) {
        super(plugin, type);
        this.material = material;
        this.damageBonus = damageBonus;
        this.speedBonus = speedBonus;
        this.knockbackResistBonus = knockbackResistBonus;
        this.lore = lore;
    }

    @Override
    protected ItemStack createBaseItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Preserve vanilla attribute modifiers and then add our bonuses
            Multimap<Attribute, AttributeModifier> defaults = meta.getAttributeModifiers();
            if (defaults != null) {
                for (Map.Entry<Attribute, AttributeModifier> entry : defaults.entries()) {
                    meta.addAttributeModifier(entry.getKey(), entry.getValue());
                }
            }

            if (damageBonus != 0.0) {
                meta.addAttributeModifier(
                        Attribute.ATTACK_DAMAGE,
                        new AttributeModifier(new NamespacedKey(plugin, type.name().toLowerCase(Locale.ROOT) + "_damage"),
                                damageBonus,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlotGroup.MAINHAND)
                );
            }
            if (speedBonus != 0.0) {
                meta.addAttributeModifier(
                        Attribute.ATTACK_SPEED,
                        new AttributeModifier(new NamespacedKey(plugin, type.name().toLowerCase(Locale.ROOT) + "_speed"),
                                speedBonus,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlotGroup.MAINHAND)
                );
            }
            if (knockbackResistBonus != 0.0) {
                meta.addAttributeModifier(
                        Attribute.KNOCKBACK_RESISTANCE,
                        new AttributeModifier(new NamespacedKey(plugin, type.name().toLowerCase(Locale.ROOT) + "_kb"),
                                knockbackResistBonus,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlotGroup.MAINHAND)
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
