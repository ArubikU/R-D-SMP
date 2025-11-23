package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ThornShield extends CustomItem {

    public ThornShield(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.THORN_SHIELD);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.SHIELD);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Escudo con espinas afiladas", "Thorns III", "Unbreaking III");
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = super.getItemStack();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.THORNS, 3, true);
            meta.addEnchant(Enchantment.UNBREAKING, 3, true);
            item.setItemMeta(meta);
        }
        return item;
    }
}
