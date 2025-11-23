package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FatalAttractionModifier extends Modifier {

    private BukkitRunnable task;

    public FatalAttractionModifier(RollAndDeathSMP plugin) {
        super(plugin, "Atracción Fatal", ModifierType.CHAOS, "Todos los mobs son atraídos hacia el jugador más cercano.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (Entity entity : player.getNearbyEntities(30, 10, 30)) {
                        if (entity instanceof Creature creature) {
                            if (creature.getTarget() == null) {
                                creature.setTarget(player);
                            }
                        }
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 40L); // Every 2 seconds
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
