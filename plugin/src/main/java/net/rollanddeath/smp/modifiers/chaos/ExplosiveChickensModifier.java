package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ExplosiveChickensModifier extends Modifier {

    private BukkitRunnable task;

    public ExplosiveChickensModifier(RollAndDeathSMP plugin) {
        super(plugin, "Gallinas Explosivas", ModifierType.CHAOS, "Las gallinas explotan si te acercas.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (Entity entity : player.getNearbyEntities(3, 3, 3)) {
                        if (entity instanceof Chicken) {
                            entity.getWorld().createExplosion(entity.getLocation(), 2.0f, false, false);
                            entity.remove();
                        }
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 10L); // Check every 0.5s
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
