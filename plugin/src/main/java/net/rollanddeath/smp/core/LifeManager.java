package net.rollanddeath.smp.core;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LifeManager {

    private final JavaPlugin plugin;
    private final NamespacedKey livesKey;
    private static final int DEFAULT_LIVES = 3;
    private final File livesFile;
    private final YamlConfiguration livesConfig;
    private final Map<UUID, Integer> storedLives = new HashMap<>();

    public LifeManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.livesKey = new NamespacedKey(plugin, "lives");
        this.livesFile = new File(plugin.getDataFolder(), "lives.yml");
        this.livesConfig = YamlConfiguration.loadConfiguration(livesFile);
        loadStoredLives();
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
        rememberLives(player.getUniqueId(), lives, true);
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

    public int getStoredLives(UUID uuid) {
        return storedLives.getOrDefault(uuid, DEFAULT_LIVES);
    }

    private void rememberLives(UUID uuid, int lives, boolean save) {
        storedLives.put(uuid, Math.max(0, lives));
        livesConfig.set(uuid.toString(), Math.max(0, lives));
        if (save) {
            saveStoredLives();
        }
    }

    private void loadStoredLives() {
        for (String key : livesConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int value = livesConfig.getInt(key, DEFAULT_LIVES);
                storedLives.put(uuid, Math.max(0, value));
            } catch (IllegalArgumentException ignored) {
                // skip bad entries
            }
        }
    }

    private void saveStoredLives() {
        try {
            livesConfig.save(livesFile);
        } catch (IOException e) {
            plugin.getLogger().warning("No se pudo guardar lives.yml: " + e.getMessage());
        }
    }
}
