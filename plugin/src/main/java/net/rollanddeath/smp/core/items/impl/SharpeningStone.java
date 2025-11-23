package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SharpeningStone extends CustomItem {

    public SharpeningStone(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.SHARPENING_STONE);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.FLINT);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Úsala en un yunque para reparar items", "sin coste de experiencia");
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        ItemStack first = event.getInventory().getItem(0);
        ItemStack second = event.getInventory().getItem(1);

        if (first != null && second != null && isItem(second)) {
            // Logic to repair item would go here, but Anvil API is tricky.
            // For simplicity, let's say it just fully repairs the item if it's damageable.
            if (first.getType().getMaxDurability() > 0) {
                ItemStack result = first.clone();
                org.bukkit.inventory.meta.Damageable meta = (org.bukkit.inventory.meta.Damageable) result.getItemMeta();
                meta.setDamage(0);
                result.setItemMeta(meta);
                event.setResult(result);
            }
        }
    }
}
