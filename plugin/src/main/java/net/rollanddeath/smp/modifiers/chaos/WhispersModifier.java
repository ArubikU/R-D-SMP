package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class WhispersModifier extends Modifier {

    private BukkitRunnable task;
    private final Random random = new Random();

    public WhispersModifier(RollAndDeathSMP plugin) {
        super(plugin, "Susurros", ModifierType.CHAOS, "Sonidos de cueva aleatorios para todos.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (random.nextDouble() < 0.02) { // 2% chance per second
                        player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1.0f, 1.0f);
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
