package net.rollanddeath.smp.core.monetization;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TrailListener implements Listener {

    private final MonetizationManager monetizationManager;

    public TrailListener(MonetizationManager monetizationManager) {
        this.monetizationManager = monetizationManager;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom() == null || event.getTo() == null) return;
        monetizationManager.spawnTrailIfAny(event.getPlayer(), event.getFrom(), event.getTo());
    }
}
