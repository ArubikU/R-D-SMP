package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class SlimeRainModifier extends Modifier {

    private BukkitRunnable task;
    private final Random random = new Random();

    public SlimeRainModifier(RollAndDeathSMP plugin) {
        super(plugin, "Lluvia de Slimes", ModifierType.CHAOS, "Lluvia spawnea slimes del cielo.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getWorld().hasStorm()) {
                        if (random.nextDouble() < 0.1) { // 10% chance per second per player
                            Location loc = player.getLocation().add(random.nextInt(10) - 5, 10, random.nextInt(10) - 5);
                            if (loc.getY() < 320) { // Sanity check
                                player.getWorld().spawnEntity(loc, EntityType.SLIME);
                            }
                        }
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
