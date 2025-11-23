package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ScorchingSunModifier extends Modifier {

    private BukkitRunnable task;

    public ScorchingSunModifier(JavaPlugin plugin) {
        super(plugin, "Sol Abrasador", ModifierType.CURSE, "El sol quema a los jugadores expuestos.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (shouldBurn(player)) {
                        player.setFireTicks(60); // Burn for 3 seconds (refreshed every 1s)
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    private boolean shouldBurn(Player player) {
        World world = player.getWorld();
        if (world.getEnvironment() != World.Environment.NORMAL) return false;
        
        long time = world.getTime();
        boolean isDay = time < 12300 || time > 23850;
        if (!isDay) return false;

        if (world.hasStorm()) return false;

        Block block = player.getLocation().getBlock();
        return block.getLightFromSky() == 15;
    }
}
