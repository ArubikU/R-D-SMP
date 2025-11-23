package net.rollanddeath.smp.core;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

public class LifeManager {

    private final JavaPlugin plugin;
    private final NamespacedKey livesKey;
    private static final int DEFAULT_LIVES = 3;

    public LifeManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.livesKey = new NamespacedKey(plugin, "lives");
    }

    public int getLives(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        if (!container.has(livesKey, PersistentDataType.INTEGER)) {
            setLives(player, DEFAULT_LIVES);
            return DEFAULT_LIVES;
        }
        return container.get(livesKey, PersistentDataType.INTEGER);
    }

    public void setLives(Player player, int lives) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        container.set(livesKey, PersistentDataType.INTEGER, lives);
    }

    public void removeLife(Player player) {
        int current = getLives(player);
        setLives(player, Math.max(0, current - 1));
    }

    public void addLife(Player player) {
        int current = getLives(player);
        setLives(player, current + 1);
    }

    public boolean isEliminated(Player player) {
        return getLives(player) <= 0;
    }

    public void revive(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            setLives(player, 1);
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(Component.text("¡Has sido revivido!", NamedTextColor.GREEN));
        } else {
            List<String> pending = plugin.getConfig().getStringList("pending_revives");
            if (pending == null) pending = new ArrayList<>();
            if (!pending.contains(uuid.toString())) {
                pending.add(uuid.toString());
                plugin.getConfig().set("pending_revives", pending);
                plugin.saveConfig();
            }
        }
    }

    public void checkPendingRevive(Player player) {
        List<String> pending = plugin.getConfig().getStringList("pending_revives");
        if (pending != null && pending.contains(player.getUniqueId().toString())) {
            setLives(player, 1);
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(Component.text("¡Has sido revivido mientras estabas desconectado!", NamedTextColor.GREEN));
            
            pending.remove(player.getUniqueId().toString());
            plugin.getConfig().set("pending_revives", pending);
            plugin.saveConfig();
        }
    }
}
