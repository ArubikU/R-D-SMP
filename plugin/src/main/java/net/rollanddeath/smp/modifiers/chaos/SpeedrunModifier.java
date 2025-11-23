package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class SpeedrunModifier extends Modifier {

    private BukkitRunnable task;

    public SpeedrunModifier(RollAndDeathSMP plugin) {
        super(plugin, "Speedrun", ModifierType.CHAOS, "El tiempo pasa x2 de rápido.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    // Add extra time to simulate faster day/night cycle
                    // Default is 1 tick per tick. We add 1 more to make it 2x.
                    world.setTime(world.getTime() + 1);
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 1L);
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
