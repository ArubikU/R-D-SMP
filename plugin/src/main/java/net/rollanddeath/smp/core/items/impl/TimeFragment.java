package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TimeFragment extends CustomItem {

    public TimeFragment(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.TIME_FRAGMENT);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.AMETHYST_SHARD);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Craftea el reloj de reinicio de evento.");
    }
}
