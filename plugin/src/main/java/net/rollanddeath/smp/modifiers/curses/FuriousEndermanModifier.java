package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FuriousEndermanModifier extends Modifier {

    private BukkitRunnable task;

    public FuriousEndermanModifier(JavaPlugin plugin) {
        super(plugin, "Enderman Furiosos", ModifierType.CURSE, "Mirar a cualquier lado puede aggrear Endermans.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) continue;
                    for (Entity entity : player.getNearbyEntities(32, 32, 32)) {
                        if (entity instanceof Enderman enderman) {
                            if (enderman.getTarget() == null) {
                                enderman.setTarget(player);
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
        if (task != null) task.cancel();
    }
}
