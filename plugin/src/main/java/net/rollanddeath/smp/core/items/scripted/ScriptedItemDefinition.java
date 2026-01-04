package net.rollanddeath.smp.core.items.scripted;

import net.rollanddeath.smp.core.modifiers.scripted.ModifierRule;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.List;
import java.util.Map;

public record ScriptedItemDefinition(
    String id,
    Material baseMaterial,
    String displayName,
    Integer customModelData,
    Integer maxStackSize,
    Integer maxDamage,
    List<String> lore,
    String leatherColor,
    List<PdcSpec> pdc,
    List<EnchantmentSpec> enchantments,
    List<AttributeSpec> attributes,
    Map<String, ModifierRule> events
) {

    public record PdcSpec(
        String key,
        String dataType,
        Object value
    ) {
    }

    public record EnchantmentSpec(
        Enchantment enchantment,
        int level
    ) {
    }

    public record AttributeSpec(
        Attribute attribute,
        double amount,
        AttributeModifier.Operation operation,
        EquipmentSlotGroup slot,
        String key
    ) {
    }
}
