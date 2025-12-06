package net.rollanddeath.smp.core.game;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks kill points per player. Stored persistently in killpoints.yml.
 */
public class KillPointsManager {

    private final RollAndDeathSMP plugin;
    private final File dataFile;
    private final Map<UUID, Integer> points = new HashMap<>();

    public KillPointsManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "killpoints.yml");
        plugin.getDataFolder().mkdirs();
        load();
    }

    public int addKill(UUID playerId) {
        int newValue = points.getOrDefault(playerId, 0) + 1;
        points.put(playerId, newValue);
        save();
        return newValue;
    }

    public int getPoints(UUID playerId) {
        return points.getOrDefault(playerId, 0);
    }

    public void setPoints(UUID playerId, int value) {
        points.put(playerId, Math.max(0, value));
        save();
    }

    public void shutdown() {
        save();
    }

    private void load() {
        if (!dataFile.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        for (String key : cfg.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                int value = cfg.getInt(key, 0);
                points.put(id, Math.max(0, value));
            } catch (IllegalArgumentException ignored) {
                // skip bad entries
            }
        }
    }

    private void save() {
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : points.entrySet()) {
            cfg.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            cfg.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("No se pudo guardar killpoints.yml: " + e.getMessage());
        }
    }
}
