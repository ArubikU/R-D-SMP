package net.rollanddeath.smp.core.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnnounceManager {

    private static final long MIN_DELAY_TICKS = 20L;

    private final RollAndDeathSMP plugin;
    private final MiniMessage mini = MiniMessage.miniMessage();
    private final List<Announcement> announcements = new ArrayList<>();

    private long defaultIntervalTicks = 600L * 20L;
    private long initialDelayTicks = 60L * 20L;
    private BukkitTask scheduledTask;
    private int nextIndex = 0;

    public AnnounceManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        stop();
        announcements.clear();
        loadFile();
        nextIndex = 0;
        start();
    }

    public void stop() {
        if (scheduledTask != null) {
            scheduledTask.cancel();
            scheduledTask = null;
        }
    }

    private void start() {
        if (announcements.isEmpty()) {
            return;
        }
        scheduleNext(Math.max(MIN_DELAY_TICKS, initialDelayTicks));
    }

    private void loadFile() {
        File folder = plugin.getDataFolder();
        if (!folder.exists() && !folder.mkdirs()) {
            plugin.getLogger().warning("No se pudo crear la carpeta de datos para announces.yml");
        }

        File file = new File(folder, "announces.yml");
        if (!file.exists()) {
            plugin.saveResource("announces.yml", false);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        defaultIntervalTicks = ticksFromSeconds(config.getDouble("default_interval_seconds", 600D));
        initialDelayTicks = ticksFromSeconds(config.getDouble("initial_delay_seconds", 60D));

        List<Map<?, ?>> entries = config.getMapList("announcements");
        if (entries == null || entries.isEmpty()) {
            plugin.getLogger().warning("announces.yml no contiene anuncios configurados.");
            return;
        }

        for (Map<?, ?> raw : entries) {
            Object rawMessage = raw.get("message");
            if (rawMessage == null) {
                continue;
            }

            String message = String.valueOf(rawMessage).trim();
            if (message.isEmpty()) {
                continue;
            }

            double intervalSeconds = defaultIntervalTicks / 20D;
            Object rawInterval = raw.get("interval_seconds");
            if (rawInterval != null) {
                intervalSeconds = toDouble(rawInterval, intervalSeconds);
            }

            long intervalTicks = Math.max(MIN_DELAY_TICKS, ticksFromSeconds(intervalSeconds));
            Component component = mini.deserialize(message);
            announcements.add(new Announcement(component, intervalTicks));
        }
    }

    private void scheduleNext(long delayTicks) {
        stop();
        scheduledTask = Bukkit.getScheduler().runTaskLater(plugin, this::broadcastNext, delayTicks);
    }

    private void broadcastNext() {
        if (announcements.isEmpty()) {
            return;
        }

        Announcement announcement = announcements.get(nextIndex);
        nextIndex = (nextIndex + 1) % announcements.size();
        Bukkit.broadcast(announcement.component());
        scheduleNext(Math.max(MIN_DELAY_TICKS, announcement.intervalTicks()));
    }

    private long ticksFromSeconds(double seconds) {
        if (Double.isNaN(seconds) || Double.isInfinite(seconds) || seconds <= 0) {
            return defaultIntervalTicks;
        }
        return (long) Math.max(MIN_DELAY_TICKS, Math.round(seconds * 20D));
    }

    private double toDouble(Object value, double fallback) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private record Announcement(Component component, long intervalTicks) {}
}
