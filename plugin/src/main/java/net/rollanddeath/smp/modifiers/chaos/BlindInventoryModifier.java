package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlindInventoryModifier extends Modifier {

    public BlindInventoryModifier(RollAndDeathSMP plugin) {
        super(plugin, "Inventario Ciego", ModifierType.CHAOS, "Los tooltips de los items están ocultos.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        updateAll(true);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        updateAll(false);
    }

    private void updateAll(boolean hide) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            updateInventory(p.getInventory(), hide);
        }
    }

    private void updateInventory(Inventory inv, boolean hide) {
        for (ItemStack item : inv.getContents()) {
            updateItem(item, hide);
        }
    }

    private void updateItem(ItemStack item, boolean hide) {
        if (item == null || item.getType().isAir()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        
        // Requires Paper/Spigot 1.20.5+
        try {
            meta.setHideTooltip(hide);
            item.setItemMeta(meta);
        } catch (NoSuchMethodError e) {
            // Fallback for older versions or if method is missing
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        updateInventory(event.getPlayer().getInventory(), true);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            updateItem(event.getItem().getItemStack(), true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        updateItem(event.getCurrentItem(), true);
        updateItem(event.getCursor(), true);
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        updateItem(event.getInventory().getResult(), true);
    }
}
