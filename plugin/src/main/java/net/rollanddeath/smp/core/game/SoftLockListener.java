package net.rollanddeath.smp.core.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class SoftLockListener implements Listener {

    private final RollAndDeathSMP plugin;

    public SoftLockListener(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent event) {
        if (event.getTo() == null || event.getTo().getWorld() == null) {
            return;
        }

        World.Environment targetEnv = event.getTo().getWorld().getEnvironment();
        GameManager gameManager = plugin.getGameManager();

        if (targetEnv == World.Environment.NETHER && !gameManager.isNetherUnlocked()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("El Nether se desbloquea el día " + gameManager.getNetherUnlockDay() + ".", NamedTextColor.RED));
            return;
        }

        if (targetEnv == World.Environment.THE_END && !gameManager.isEndUnlocked()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("El End se desbloquea el día " + gameManager.getEndUnlockDay() + ".", NamedTextColor.RED));
        }
    }
}
