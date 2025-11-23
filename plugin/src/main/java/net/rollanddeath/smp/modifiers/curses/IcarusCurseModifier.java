package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class IcarusCurseModifier extends Modifier {

    private BukkitRunnable task;

    public IcarusCurseModifier(JavaPlugin plugin) {
        super(plugin, "Maldición de Ícaro", ModifierType.CURSE, "Si subes por encima de Y=150, empiezas a quemarte.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getLocation().getY() > 150) {
                        player.setFireTicks(60);
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
