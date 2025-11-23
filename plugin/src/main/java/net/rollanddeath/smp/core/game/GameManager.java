package net.rollanddeath.smp.core.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Bukkit;

import java.time.LocalDate;

public class GameManager {

    private final RollAndDeathSMP plugin;
    private int currentDay = 0;
    private LocalDate lastCheckDate;

    public GameManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        loadDay();
        startRealTimeDayCycleTask();
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
        plugin.saveConfig();
        
        Bukkit.broadcast(Component.text("¡El Día " + day + " ha comenzado!", NamedTextColor.GOLD));
        
        // Check for Day 31 (Permadeath)
        if (day >= 31) {
            Bukkit.broadcast(Component.text("⚠ ¡ATENCIÓN! SE HA ALCANZADO EL DÍA 31.", NamedTextColor.DARK_RED));
            Bukkit.broadcast(Component.text("LA MUERTE PERMANENTE ESTÁ ACTIVA.", NamedTextColor.RED));
            // Logic for permadeath is handled in PlayerDeathListener checking this value
        }

        // Spin Roulette
        plugin.getModifierManager().spinRoulette();
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public boolean isPermadeathActive() {
        return currentDay >= 31;
    }
}
