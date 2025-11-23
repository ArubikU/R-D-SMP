package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class DisorientationModifier extends Modifier {

    private BukkitRunnable task;
    private final Random random = new Random();

    public DisorientationModifier(JavaPlugin plugin) {
        super(plugin, "Desorientación", ModifierType.CURSE, "La brújula apunta a direcciones aleatorias, no al spawn.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    int x = random.nextInt(10000) - 5000;
                    int z = random.nextInt(10000) - 5000;
                    player.setCompassTarget(new Location(player.getWorld(), x, 64, z));
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 100L); // Every 5 seconds
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null) task.cancel();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setCompassTarget(player.getWorld().getSpawnLocation());
        }
    }
}
