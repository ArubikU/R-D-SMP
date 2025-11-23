package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DeafnessModifier extends Modifier {

    private BukkitRunnable task;

    public DeafnessModifier(JavaPlugin plugin) {
        super(plugin, "Sordera", ModifierType.CURSE, "Todos los sonidos del juego desactivados.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.stopAllSounds();
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 20L); // Every second
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null) task.cancel();
    }
}
