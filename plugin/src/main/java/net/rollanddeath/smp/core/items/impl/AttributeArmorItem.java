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
            // Mojang/Paper 1.20.5+ (componentes): en cuanto seteas modifiers, puedes perder los defaults.
            // En vez de "leer" defaults (a veces sale vacío/0), calculamos base vanilla manualmente y sumamos.
            ArmorDefaults defaults = getVanillaArmorDefaults(material);

            double totalArmor = defaults.armor() + armorBonus;
            double totalToughness = defaults.toughness() + toughnessBonus;
            double totalKb = defaults.knockbackResist() + knockbackResistBonus;

            if (totalArmor != 0.0) {
                meta.addAttributeModifier(
                        Attribute.ARMOR,
                        new AttributeModifier(
                                new NamespacedKey(plugin, type.name().toLowerCase(Locale.ROOT) + "_armor"),
                                totalArmor,
                                AttributeModifier.Operation.ADD_NUMBER,
                                slotGroup
                        )
                );
            }
            if (totalToughness != 0.0) {
                meta.addAttributeModifier(
                        Attribute.ARMOR_TOUGHNESS,
                        new AttributeModifier(
                                new NamespacedKey(plugin, type.name().toLowerCase(Locale.ROOT) + "_toughness"),
                                totalToughness,
                                AttributeModifier.Operation.ADD_NUMBER,
                                slotGroup
                        )
                );
            }
            if (totalKb != 0.0) {
                meta.addAttributeModifier(
                        Attribute.KNOCKBACK_RESISTANCE,
                        new AttributeModifier(
                                new NamespacedKey(plugin, type.name().toLowerCase(Locale.ROOT) + "_kb"),
                                totalKb,
                                AttributeModifier.Operation.ADD_NUMBER,
                                slotGroup
                        )
                );
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private record ArmorDefaults(double armor, double toughness, double knockbackResist) {}

    private static ArmorDefaults getVanillaArmorDefaults(Material material) {
        return switch (material) {
            // Leather
            case LEATHER_HELMET -> new ArmorDefaults(1.0, 0.0, 0.0);
            case LEATHER_CHESTPLATE -> new ArmorDefaults(3.0, 0.0, 0.0);
            case LEATHER_LEGGINGS -> new ArmorDefaults(2.0, 0.0, 0.0);
            case LEATHER_BOOTS -> new ArmorDefaults(1.0, 0.0, 0.0);

            // Chainmail
            case CHAINMAIL_HELMET -> new ArmorDefaults(2.0, 0.0, 0.0);
            case CHAINMAIL_CHESTPLATE -> new ArmorDefaults(5.0, 0.0, 0.0);
            case CHAINMAIL_LEGGINGS -> new ArmorDefaults(4.0, 0.0, 0.0);
            case CHAINMAIL_BOOTS -> new ArmorDefaults(1.0, 0.0, 0.0);

            // Gold
            case GOLDEN_HELMET -> new ArmorDefaults(2.0, 0.0, 0.0);
            case GOLDEN_CHESTPLATE -> new ArmorDefaults(5.0, 0.0, 0.0);
            case GOLDEN_LEGGINGS -> new ArmorDefaults(3.0, 0.0, 0.0);
            case GOLDEN_BOOTS -> new ArmorDefaults(1.0, 0.0, 0.0);

            // Iron
            case IRON_HELMET -> new ArmorDefaults(2.0, 0.0, 0.0);
            case IRON_CHESTPLATE -> new ArmorDefaults(6.0, 0.0, 0.0);
            case IRON_LEGGINGS -> new ArmorDefaults(5.0, 0.0, 0.0);
            case IRON_BOOTS -> new ArmorDefaults(2.0, 0.0, 0.0);

            // Diamond
            case DIAMOND_HELMET -> new ArmorDefaults(3.0, 2.0, 0.0);
            case DIAMOND_CHESTPLATE -> new ArmorDefaults(8.0, 2.0, 0.0);
            case DIAMOND_LEGGINGS -> new ArmorDefaults(6.0, 2.0, 0.0);
            case DIAMOND_BOOTS -> new ArmorDefaults(3.0, 2.0, 0.0);

            // Netherite
            case NETHERITE_HELMET -> new ArmorDefaults(3.0, 3.0, 0.1);
            case NETHERITE_CHESTPLATE -> new ArmorDefaults(8.0, 3.0, 0.1);
            case NETHERITE_LEGGINGS -> new ArmorDefaults(6.0, 3.0, 0.1);
            case NETHERITE_BOOTS -> new ArmorDefaults(3.0, 3.0, 0.1);

            default -> new ArmorDefaults(0.0, 0.0, 0.0);
        };
    }

    @Override
    protected List<String> getLore() {
        return lore;
    }
}
