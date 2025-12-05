package net.rollanddeath.smp.core.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.core.combat.ReanimationManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class PlayerHudManager extends BukkitRunnable implements Listener {

    private static final double TICKS_PER_SECOND = 20.0D;

    private static final Direction[] DIRECTIONS = new Direction[] {
        new Direction("Sur", "S"),
        new Direction("Suroeste", "SO"),
        new Direction("Oeste", "O"),
        new Direction("Noroeste", "NO"),
        new Direction("Norte", "N"),
        new Direction("Noreste", "NE"),
        new Direction("Este", "E"),
        new Direction("Sureste", "SE")
    };

    private final ReanimationManager reanimationManager;
    private final Map<UUID, Location> previousLocations = new HashMap<>();
    private final double secondsPerUpdate;

    public PlayerHudManager(ReanimationManager reanimationManager, int updatePeriodTicks) {
        this.reanimationManager = reanimationManager;
        int clampedPeriod = Math.max(1, updatePeriodTicks);
        this.secondsPerUpdate = clampedPeriod / TICKS_PER_SECOND;
        Bukkit.getOnlinePlayers().forEach(player -> previousLocations.put(player.getUniqueId(), player.getLocation().clone()));
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isValid() || player.getGameMode() == GameMode.SPECTATOR) {
                previousLocations.put(player.getUniqueId(), player.getLocation().clone());
                continue;
            }

            if (reanimationManager != null && reanimationManager.isDowned(player)) {
                previousLocations.put(player.getUniqueId(), player.getLocation().clone());
                continue;
            }

            Component hud = buildHud(player);
            if (hud != null) {
                player.sendActionBar(hud);
            }

            previousLocations.put(player.getUniqueId(), player.getLocation().clone());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        previousLocations.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation().clone());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        previousLocations.remove(event.getPlayer().getUniqueId());
    }

    private Component buildHud(Player player) {
        Direction direction = resolveDirection(player.getLocation().getYaw());
        Component compass = Component.text("Rumbo: ", NamedTextColor.GRAY)
            .append(Component.text(direction.fullName(), NamedTextColor.AQUA))
            .append(Component.text(" (" + direction.shortName() + ")", NamedTextColor.DARK_AQUA));

        if (player.isGliding()) {
            double speed = computeSpeed(player);
            Component speedComponent = Component.text(
                String.format(Locale.ROOT, "Velocidad: %.1f m/s", speed),
                NamedTextColor.GOLD
            );
            return Component.text()
                .append(speedComponent)
                .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                .append(compass)
                .build();
        }

        return compass;
    }

    private double computeSpeed(Player player) {
        Location previous = previousLocations.get(player.getUniqueId());
        Location current = player.getLocation();
        if (previous == null || current.getWorld() == null || !current.getWorld().equals(previous.getWorld())) {
            return 0.0D;
        }

        double distance = previous.toVector().distance(current.toVector());
        if (secondsPerUpdate <= 0.0D) {
            return 0.0D;
        }
        return distance / secondsPerUpdate;
    }

    private Direction resolveDirection(float yaw) {
        float normalized = yaw % 360.0F;
        if (normalized < 0.0F) {
            normalized += 360.0F;
        }
        int index = Math.round(normalized / 45.0F) % DIRECTIONS.length;
        return DIRECTIONS[index];
    }

    private record Direction(String fullName, String shortName) { }
}
