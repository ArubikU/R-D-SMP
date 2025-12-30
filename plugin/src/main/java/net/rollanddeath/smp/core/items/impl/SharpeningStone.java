package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.AnvilInventory;

import java.util.List;

public class SharpeningStone extends CustomItem {

    public SharpeningStone(RollAndDeathSMP plugin) {
        super(plugin, "SHARPENING_STONE");
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.FLINT);
    }

    @Override
    public String getDisplayName() {
        return "Piedra de Afilar";
    }

    @Override
    protected Integer getCustomModelData() {
        return 710002;
    }

    @Override
    protected List<String> getLore() {
        return List.of("Úsala en un yunque para reparar items", "sin coste de experiencia");
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        ItemStack first = event.getInventory().getItem(0);
        ItemStack second = event.getInventory().getItem(1);

        if (first != null && second != null && isItem(second) && first.getItemMeta() instanceof Damageable damageable) {
            ItemStack result = first.clone();
            Damageable meta = (Damageable) result.getItemMeta();
            meta.setDamage(0);
            result.setItemMeta(meta);
            event.setResult(result);
            event.getInventory().setRepairCost(0); // allow take without XP
        }
    }

    @EventHandler
    public void onResultTake(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof AnvilInventory anvil)) return;
        if (event.getSlotType() != InventoryType.SlotType.RESULT) return;

        ItemStack first = anvil.getItem(0);
        ItemStack second = anvil.getItem(1);
        ItemStack result = event.getCurrentItem();

        if (first == null || second == null || result == null) return;
        if (!isItem(second)) return;
        if (!(first.getItemMeta() instanceof Damageable)) return;

        // Consume one sharpening stone and refresh repaired item to prevent ghost results.
        anvil.setRepairCost(0);
        if (second.getAmount() <= 1) {
            anvil.setItem(1, null);
        } else {
            ItemStack copy = second.clone();
            copy.setAmount(second.getAmount() - 1);
            anvil.setItem(1, copy);
        }

        // Ensure the output is fully repaired and placed in cursor.
        ItemStack repaired = first.clone();
        Damageable meta = (Damageable) repaired.getItemMeta();
        meta.setDamage(0);
        repaired.setItemMeta(meta);
        event.setCurrentItem(repaired);
    }
}
