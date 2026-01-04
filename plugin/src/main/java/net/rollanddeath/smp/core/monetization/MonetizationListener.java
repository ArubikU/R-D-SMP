package net.rollanddeath.smp.core.monetization;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class MonetizationListener implements Listener {

    private final MonetizationManager monetizationManager;

    public MonetizationListener(MonetizationManager monetizationManager) {
        this.monetizationManager = monetizationManager;
    }

    @EventHandler
    public void onEnderInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock().getType() != Material.ENDER_CHEST) return;

        event.setCancelled(true);
        monetizationManager.openEnderChest(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        monetizationManager.saveIfEnderChest(event.getInventory());
        monetizationManager.saveIfBackpack(event.getInventory());
    }
}
