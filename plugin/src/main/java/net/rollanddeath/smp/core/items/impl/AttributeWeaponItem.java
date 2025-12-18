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
            // Mojang/Paper 1.20.5+ (componentes): al tocar modifiers, puedes perder defaults.
            // En vez de intentar preservar defaults (a veces sale vacío/0), calculamos el modifier vanilla y sumamos.
            WeaponDefaults defaults = getVanillaWeaponDefaults(material);

            double totalDamageMod = defaults.attackDamageMod() + damageBonus;
            double totalSpeedMod = defaults.attackSpeedMod() + speedBonus;
            double totalKb = knockbackResistBonus;

            if (totalDamageMod != 0.0) {
                meta.addAttributeModifier(
                        Attribute.ATTACK_DAMAGE,
                        new AttributeModifier(
                                new NamespacedKey(plugin, type.name().toLowerCase(Locale.ROOT) + "_damage"),
                                totalDamageMod,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlotGroup.MAINHAND
                        )
                );
            }
            if (totalSpeedMod != 0.0) {
                meta.addAttributeModifier(
                        Attribute.ATTACK_SPEED,
                        new AttributeModifier(
                                new NamespacedKey(plugin, type.name().toLowerCase(Locale.ROOT) + "_speed"),
                                totalSpeedMod,
                                AttributeModifier.Operation.ADD_NUMBER,
                                EquipmentSlotGroup.MAINHAND
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
                                EquipmentSlotGroup.MAINHAND
                        )
                );
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private record WeaponDefaults(double attackDamageMod, double attackSpeedMod) {}

    /**
     * Devuelve los modifiers vanilla (Operation.ADD_NUMBER) para MAINHAND.
     * Nota: base player = daño 1.0, velocidad 4.0, por eso aquí guardamos el "modifier" vanilla, no el valor final.
     */
    private static WeaponDefaults getVanillaWeaponDefaults(Material material) {
        return switch (material) {
            // Swords
            case WOODEN_SWORD -> new WeaponDefaults(3.0, -2.4);
            case STONE_SWORD -> new WeaponDefaults(4.0, -2.4);
            case IRON_SWORD -> new WeaponDefaults(5.0, -2.4);
            case DIAMOND_SWORD -> new WeaponDefaults(6.0, -2.4);
            case NETHERITE_SWORD -> new WeaponDefaults(7.0, -2.4);
            case GOLDEN_SWORD -> new WeaponDefaults(3.0, -2.4);

            // Axes
            case WOODEN_AXE -> new WeaponDefaults(6.0, -3.2);
            case STONE_AXE -> new WeaponDefaults(7.0, -3.2);
            case IRON_AXE -> new WeaponDefaults(8.0, -3.1);
            case DIAMOND_AXE -> new WeaponDefaults(9.0, -3.0);
            case NETHERITE_AXE -> new WeaponDefaults(10.0, -3.0);
            case GOLDEN_AXE -> new WeaponDefaults(6.0, -3.0);

            // Pickaxes
            case WOODEN_PICKAXE -> new WeaponDefaults(1.0, -2.8);
            case STONE_PICKAXE -> new WeaponDefaults(2.0, -2.8);
            case IRON_PICKAXE -> new WeaponDefaults(3.0, -2.8);
            case DIAMOND_PICKAXE -> new WeaponDefaults(4.0, -2.8);
            case NETHERITE_PICKAXE -> new WeaponDefaults(5.0, -2.8);
            case GOLDEN_PICKAXE -> new WeaponDefaults(1.0, -2.8);

            // Shovels
            case WOODEN_SHOVEL -> new WeaponDefaults(1.5, -3.0);
            case STONE_SHOVEL -> new WeaponDefaults(2.5, -3.0);
            case IRON_SHOVEL -> new WeaponDefaults(3.5, -3.0);
            case DIAMOND_SHOVEL -> new WeaponDefaults(4.5, -3.0);
            case NETHERITE_SHOVEL -> new WeaponDefaults(5.5, -3.0);
            case GOLDEN_SHOVEL -> new WeaponDefaults(1.5, -3.0);

            // Hoes
            case WOODEN_HOE -> new WeaponDefaults(0.0, -3.0);
            case STONE_HOE -> new WeaponDefaults(1.0, -2.0);
            case IRON_HOE -> new WeaponDefaults(0.0, -1.0);
            case DIAMOND_HOE -> new WeaponDefaults(0.0, 0.0);
            case NETHERITE_HOE -> new WeaponDefaults(0.0, 0.0);
            case GOLDEN_HOE -> new WeaponDefaults(0.0, -3.0);

            // Other common melee
            case TRIDENT -> new WeaponDefaults(8.0, -2.9);

            default -> new WeaponDefaults(0.0, 0.0);
        };
    }

    @Override
    protected List<String> getLore() {
        return lore;
    }
}
