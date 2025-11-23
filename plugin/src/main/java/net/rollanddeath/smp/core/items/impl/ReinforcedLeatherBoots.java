package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ReinforcedLeatherBoots extends CustomItem {

    public ReinforcedLeatherBoots(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.REINFORCED_LEATHER_BOOTS);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.LEATHER_BOOTS);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Botas de cuero reforzadas", "Protección IV", "Irrompibilidad III");
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = super.getItemStack();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
            meta.addEnchant(Enchantment.DURABILITY, 3, true);
            item.setItemMeta(meta);
        }
        return item;
    }
}
