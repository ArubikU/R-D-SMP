package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class EternalNightModifier extends Modifier {

    private BukkitRunnable task;

    public EternalNightModifier(RollAndDeathSMP plugin) {
        super(plugin, "Noche Eterna", ModifierType.CHAOS, "El sol nunca sale.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    if (world.getEnvironment() == World.Environment.NORMAL) {
                        world.setTime(18000);
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 100L);
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
