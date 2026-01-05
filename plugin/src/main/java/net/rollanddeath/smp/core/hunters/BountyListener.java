package net.rollanddeath.smp.core.hunters;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class BountyListener implements Listener {

    private final BountyManager bountyManager;

    public BountyListener(BountyManager bountyManager) {
        this.bountyManager = bountyManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        bountyManager.payout(killer, victim);
    }
}
