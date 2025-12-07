package net.rollanddeath.smp.core.mobs;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class DailyMobRotationManager {

    private final RollAndDeathSMP plugin;
    private final List<MobType> rotation;
    private final List<MobType> activeMobs = new ArrayList<>();
    private final Random random = new Random();
    private int lastDay = -1;
    private BukkitTask spawnTask;

    public DailyMobRotationManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.rotation = new ArrayList<>(Arrays.asList(MobType.values()));
        loadState();
        startSpawnTask();
    }

    private void loadState() {
        List<String> saved = plugin.getConfig().getStringList("game.active_mobs");
        if (saved != null) {
            for (String name : saved) {
                try {
                    activeMobs.add(MobType.valueOf(name));
                } catch (IllegalArgumentException ignored) {
                    // Skip unknown mobs
                }
            }
        }
        lastDay = plugin.getConfig().getInt("game.last_mob_day", -1);
    }

    private void persist() {
        List<String> names = activeMobs.stream().map(Enum::name).collect(Collectors.toList());
        plugin.getConfig().set("game.active_mobs", names);
        plugin.getConfig().set("game.last_mob_day", lastDay);
        plugin.saveConfig();
    }

    public List<MobType> getActiveMobs() {
        return new ArrayList<>(activeMobs);
    }

    public int getLastDay() {
        return lastDay;
    }

    public void refreshForDay(int day) {
        if (rotation.isEmpty()) {
            return;
        }

        int count = Math.max(1, Math.min(day, rotation.size()));
        if (day == lastDay && activeMobs.size() == count) {
            return;
        }

        activeMobs.clear();
        for (int i = 0; i < count; i++) {
            activeMobs.add(rotation.get(i));
        }
        lastDay = day;
        persist();
    }

    private void startSpawnTask() {
        long periodTicks = 20L * 300; // every 5 minutes
        spawnTask = Bukkit.getScheduler().runTaskTimer(plugin, this::spawnActiveMobs, 200L, periodTicks);
    }

    public void stop() {
        if (spawnTask != null) {
            spawnTask.cancel();
        }
    }

    private void spawnActiveMobs() {
        if (activeMobs.isEmpty()) {
            return;
        }

        List<Player> players = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getWorld().getEnvironment() == World.Environment.NORMAL)
                .toList();
        if (players.isEmpty()) {
            return;
        }

        for (MobType mobType : activeMobs) {
            Player target = players.get(random.nextInt(players.size()));
            Location spawnLoc = findSpawnNear(target.getLocation());
            if (spawnLoc != null) {
                plugin.getMobManager().spawnMob(mobType, spawnLoc);
            }
        }
    }

    private Location findSpawnNear(Location base) {
        World world = base.getWorld();
        if (world == null) return null;

        for (int i = 0; i < 10; i++) {
            int dx = random.nextInt(33) - 16;
            int dz = random.nextInt(33) - 16;
            Location candidate = base.clone().add(dx, 0, dz);
            int y = world.getHighestBlockYAt(candidate);
            candidate.setY(y + 1);
            if (world.getBlockAt(candidate).isEmpty() && world.getBlockAt(candidate.clone().add(0, 1, 0)).isEmpty()) {
                return candidate;
            }
        }
        return null;
    }
}
