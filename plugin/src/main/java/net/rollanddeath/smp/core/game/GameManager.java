package net.rollanddeath.smp.core.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Bukkit;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class GameManager {

    private final RollAndDeathSMP plugin;
    private int currentDay = 0;
    private LocalDate lastCheckDate;
    private Instant nextEventTime;

    public GameManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        loadDay();
        startRealTimeDayCycleTask();
        startEventCountdownTask();
    }

    private void loadDay() {
        // Load from config or persistent container on a "world" entity or just config
        // Config is easier for global state
        this.currentDay = plugin.getConfig().getInt("game.current_day", 0);
        String dateStr = plugin.getConfig().getString("game.last_check_date");
        if (dateStr != null) {
            this.lastCheckDate = LocalDate.parse(dateStr);
        } else {
            this.lastCheckDate = LocalDate.now();
        }

        long nextEventEpoch = plugin.getConfig().getLong("game.next_event_time", -1L);
        if (nextEventEpoch > 0L) {
            this.nextEventTime = Instant.ofEpochMilli(nextEventEpoch);
        }

        ensureNextEventTime();
    }

    private void startRealTimeDayCycleTask() {
        // Check every minute if the day has changed
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            LocalDate today = LocalDate.now();
            if (today.isAfter(lastCheckDate)) {
                lastCheckDate = today;
                plugin.getConfig().set("game.last_check_date", today.toString());
                setDay(currentDay + 1);
            }
        }, 1200L, 1200L); // 1200 ticks = 60 seconds
    }

    public void setDay(int day) {
        this.currentDay = day;
        plugin.getConfig().set("game.current_day", day);
        scheduleNextEvent();
        plugin.saveConfig();

        Bukkit.broadcast(Component.text("¡El Día " + day + " ha comenzado!", NamedTextColor.GOLD));
        
        // Check for Day 31 (Permadeath)
        if (day >= 31) {
            Bukkit.broadcast(Component.text("⚠ ¡ATENCIÓN! SE HA ALCANZADO EL DÍA 31.", NamedTextColor.DARK_RED));
            Bukkit.broadcast(Component.text("LA MUERTE PERMANENTE ESTÁ ACTIVA.", NamedTextColor.RED));
            // Logic for permadeath is handled in PlayerDeathListener checking this value
        }
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public boolean isPermadeathActive() {
        return currentDay >= 31;
    }

    public Instant getNextEventTime() {
        ensureNextEventTime();
        return nextEventTime;
    }

    public Duration getTimeUntilNextEvent() {
        Instant target = getNextEventTime();
        Instant now = Instant.now();
        if (target.isBefore(now)) {
            return Duration.ZERO;
        }
        return Duration.between(now, target);
    }

    public void markEventExecuted() {
        scheduleNextEvent();
        plugin.saveConfig();
    }

    private void scheduleNextEvent() {
        this.nextEventTime = calculateNextEventInstant();
        plugin.getConfig().set("game.next_event_time", nextEventTime.toEpochMilli());
    }

    private void ensureNextEventTime() {
        if (this.nextEventTime == null || this.nextEventTime.isBefore(Instant.now())) {
            scheduleNextEvent();
        }
    }

    private Instant calculateNextEventInstant() {
        LocalDate baseDate = lastCheckDate != null ? lastCheckDate : LocalDate.now();
        LocalDateTime nextMidnight = baseDate.plusDays(1).atStartOfDay();
        return nextMidnight.atZone(ZoneId.systemDefault()).toInstant();
    }

    private void startEventCountdownTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            ensureNextEventTime();
            if (Instant.now().isAfter(nextEventTime)) {
                triggerScheduledEvent();
            }
        }, 20L, 20L);
    }

    private void triggerScheduledEvent() {
        // Reschedule first to avoid repeated triggers while spinRoulette() runs
        markEventExecuted();
        plugin.getModifierManager().spinRoulette();
    }
}
