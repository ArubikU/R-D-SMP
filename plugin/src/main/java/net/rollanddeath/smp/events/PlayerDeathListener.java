package net.rollanddeath.smp.events;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.LifeManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerDeathListener implements Listener {

    private final RollAndDeathSMP plugin;
    private final LifeManager lifeManager;

    public PlayerDeathListener(RollAndDeathSMP plugin, LifeManager lifeManager) {
        this.plugin = plugin;
        this.lifeManager = lifeManager;
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        // Check Permadeath (Day 31+)
        if (plugin.getGameManager().isPermadeathActive()) {
            Component banMsg = Component.text("¡Has muerto durante la MUERTE PERMANENTE!", NamedTextColor.DARK_RED);
            // Ban permanently
            Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "Muerte Permanente (Día 31+)", null, "RollAndDeath");
            player.kick(banMsg);
            
            Component announcement = Component.text("☠ ", NamedTextColor.DARK_RED)
                .append(Component.text(player.getName(), NamedTextColor.RED))
                .append(Component.text(" ha muerto permanentemente.", NamedTextColor.GRAY));
            Bukkit.broadcast(announcement);
            return;
        }

        lifeManager.removeLife(player);
        int remainingLives = lifeManager.getLives(player);

        Component message = Component.text(player.getName())
                .append(Component.text(" ha muerto. Vidas restantes: ", NamedTextColor.GRAY))
                .append(Component.text(remainingLives, remainingLives > 0 ? NamedTextColor.GREEN : NamedTextColor.RED));
        
        event.deathMessage(message);

        if (remainingLives <= 0) {
            // Ban logic or Spectator logic
            Component eliminationMsg = Component.text("¡")
                    .append(Component.text(player.getName(), NamedTextColor.RED))
                    .append(Component.text(" ha sido ELIMINADO del SMP!", NamedTextColor.DARK_RED));
            player.getServer().sendMessage(eliminationMsg);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (lifeManager.isEliminated(player)) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(Component.text("Has perdido todas tus vidas. Ahora eres un espectador.", NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Initialize lives if new
        lifeManager.getLives(player);
        
        // Check for pending revives
        lifeManager.checkPendingRevive(player);

        if (lifeManager.isEliminated(player)) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(Component.text("Sigues en el Limbo. Espera a ser revivido.", NamedTextColor.GRAY));
        }
    }
}
