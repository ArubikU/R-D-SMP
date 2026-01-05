package net.rollanddeath.smp.core.shops;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class ItemDescriptor {

    private final String material;
    private final Map<String, Integer> enchants;
    private final String customId;

    public ItemDescriptor(String material, Map<String, Integer> enchants, String customId) {
        this.material = material;
        this.enchants = enchants == null ? Collections.emptyMap() : Collections.unmodifiableMap(new HashMap<>(enchants));
        this.customId = customId;
    }

    public String material() {
        return material;
    }

    public Map<String, Integer> enchants() {
        return enchants;
    }

    public String customId() {
        return customId;
    }

    public static ItemDescriptor fromItem(RollAndDeathSMP plugin, ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) return null;
        Map<String, Integer> enc = new HashMap<>();
        stack.getEnchantments().forEach((ench, level) -> enc.put(ench.getKey().getKey(), level));

        String cid = null;
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            cid = pdc.get(new org.bukkit.NamespacedKey(plugin, "custom_item_id"), PersistentDataType.STRING);
        }

        return new ItemDescriptor(stack.getType().name(), enc, cid);
    }

    public ItemStack toItem(int amount) {
        Material mat = Material.matchMaterial(material);
        if (mat == null) return null;
        ItemStack stack = new ItemStack(mat, amount);
        if (!enchants.isEmpty()) {
            ItemMeta meta = stack.getItemMeta();
            if (meta != null) {
                for (Map.Entry<String, Integer> entry : enchants.entrySet()) {
                    Enchantment ench = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(entry.getKey()));
                    if (ench != null) {
                        meta.addEnchant(ench, entry.getValue(), true);
                    }
                }
                stack.setItemMeta(meta);
            }
        }
        return stack;
    }

    public Component format() {
        String base = material.toLowerCase(Locale.ROOT);
        StringJoiner joiner = new StringJoiner(", ");
        enchants.forEach((name, level) -> joiner.add(name + " " + level));
        String enchText = joiner.length() == 0 ? "" : " | " + joiner;
        String customText = customId != null ? " [" + customId + "]" : "";
        return Component.text(base + customText + enchText, NamedTextColor.WHITE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemDescriptor that)) return false;
        return Objects.equals(material, that.material) && Objects.equals(enchants, that.enchants) && Objects.equals(customId, that.customId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, enchants, customId);
    }
}
