package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class ParanoiaModifier extends Modifier {

    private BukkitRunnable task;
    private final Random random = new Random();

    public ParanoiaModifier(JavaPlugin plugin) {
        super(plugin, "Paranoia", ModifierType.CURSE, "Sonidos falsos de Creeper y TNT expltando aleatoriamente.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (random.nextDouble() < 0.05) { // 5% chance every second
                        Sound sound = random.nextBoolean() ? Sound.ENTITY_CREEPER_PRIMED : Sound.ENTITY_TNT_PRIMED;
                        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null) task.cancel();
    }
}
