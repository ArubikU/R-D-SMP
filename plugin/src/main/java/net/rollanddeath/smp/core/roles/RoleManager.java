package net.rollanddeath.smp.core.roles;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class RoleManager implements Listener {

    private final RollAndDeathSMP plugin;
    private final Map<RoleType, Role> roles = new EnumMap<>(RoleType.class);
    private final NamespacedKey roleKey;

    private final File dataFile;
    private final Map<UUID, RoleType> pendingRoles = new HashMap<>();
    private long nextRerollAtMillis = -1L;
    private BukkitTask rerollTask;
    private final Random random = new Random();

    public RoleManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.roleKey = new NamespacedKey(plugin, "player_role");
        this.dataFile = new File(plugin.getDataFolder(), "roles-data.yml");
        plugin.getDataFolder().mkdirs();
        loadData();
        startRerollWatcher();
    }

    public void registerRole(Role role) {
        roles.put(role.getType(), role);
        role.onEnable();
    }

    public RoleType getPlayerRole(Player player) {
        String roleName = player.getPersistentDataContainer().get(roleKey, PersistentDataType.STRING);
        if (roleName == null) return null;
        try {
            return RoleType.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void setPlayerRole(Player player, RoleType roleType) {
        if (roleType == null) {
            player.getPersistentDataContainer().remove(roleKey);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Tu rol ha sido eliminado."));
        } else {
            player.getPersistentDataContainer().set(roleKey, PersistentDataType.STRING, roleType.name());
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Tu nuevo rol es: <bold>" + roleType.getName()));
            player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>" + roleType.getDescription()));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        applyPending(event.getPlayer());
    }

    public void rerollAllRoles() {
        RoleType[] pool = RoleType.values();

        // Online players: assign immediately
        for (Player player : Bukkit.getOnlinePlayers()) {
            RoleType randomRole = pool[random.nextInt(pool.length)];
            setPlayerRole(player, randomRole);
        }

        // Offline players: store pending assignment
        for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
            if (offline.isOnline()) continue;
            UUID id = offline.getUniqueId();
            RoleType randomRole = pool[random.nextInt(pool.length)];
            pendingRoles.put(id, randomRole);
        }

        persistData();
        setNextRerollFromNow();
    }

    public long getNextRerollAtMillis() {
        return nextRerollAtMillis;
    }

    public void setNextRerollFromNow() {
        nextRerollAtMillis = System.currentTimeMillis() + Duration.ofDays(7).toMillis();
        persistData();
    }

    private void applyPending(Player player) {
        RoleType pending = pendingRoles.remove(player.getUniqueId());
        if (pending != null) {
            setPlayerRole(player, pending);
            persistData();
        }
    }

    private void startRerollWatcher() {
        if (rerollTask != null) {
            rerollTask.cancel();
        }

        rerollTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (nextRerollAtMillis <= 0) return;
            long now = System.currentTimeMillis();
            if (now >= nextRerollAtMillis) {
                rerollAllRoles();
            }
        }, 20L * 60L, 20L * 60L * 30L); // check every 30 minutes
    }

    private void loadData() {
        if (!dataFile.exists()) {
            return;
        }

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        nextRerollAtMillis = cfg.getLong("next_reroll_at", -1L);

        ConfigurationSection pendingSection = cfg.getConfigurationSection("pending");
        if (pendingSection != null) {
            for (String key : pendingSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String roleName = pendingSection.getString(key);
                    if (roleName == null) continue;
                    RoleType type = RoleType.valueOf(roleName);
                    pendingRoles.put(uuid, type);
                } catch (IllegalArgumentException ignored) {
                    // skip bad entries
                }
            }
        }
    }

    private void persistData() {
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("next_reroll_at", nextRerollAtMillis);
        Map<String, String> serialized = new HashMap<>();
        for (Map.Entry<UUID, RoleType> entry : pendingRoles.entrySet()) {
            serialized.put(entry.getKey().toString(), entry.getValue().name());
        }
        cfg.createSection("pending", serialized);

        try {
            cfg.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("No se pudo guardar roles-data.yml: " + e.getMessage());
        }
    }

    public Role getRole(RoleType type) {
        return roles.get(type);
    }
}
