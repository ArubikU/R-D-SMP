package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class SlowmotionModifier extends Modifier {

    private BukkitRunnable task;

    public SlowmotionModifier(RollAndDeathSMP plugin) {
        super(plugin, "Slowmotion", ModifierType.CHAOS, "El tiempo pasa x0.5 de lento.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        }

        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    // Advance time by 1 tick every 2 ticks (0.5 speed)
                    world.setTime(world.getTime() + 1);
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 2L);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null) {
            task.cancel();
            task = null;
        }
        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        }
    }
}
