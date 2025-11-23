package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HandOfGod extends CustomItem {

    public HandOfGod(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.HAND_OF_GOD);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.NETHER_STAR);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Elimina un evento permanente de la lista. Un solo uso.");
    }
    
    // Logic handled by command or GUI
}
