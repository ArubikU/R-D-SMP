package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

public class PhotosynthesisModifier extends Modifier {

    private BukkitRunnable task;

    public PhotosynthesisModifier(JavaPlugin plugin) {
        super(plugin, "Fotosíntesis", ModifierType.BLESSING, "Recuperas hambre bajo el sol.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (canPhotosynthesize(player)) {
                        int food = player.getFoodLevel();
                        if (food < 20) {
                            player.setFoodLevel(food + 1);
                            player.setSaturation(player.getSaturation() + 1);
                        }
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 100L, 100L); // Every 5 seconds
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        if (task != null) {
            task.cancel();
        }
    }

    private boolean canPhotosynthesize(Player player) {
        long time = player.getWorld().getTime();
        boolean isDay = time > 0 && time < 12300;
        if (!isDay) return false;

        if (player.getWorld().getEnvironment() != org.bukkit.World.Environment.NORMAL) return false;
        if (player.getWorld().hasStorm()) return false;

        byte light = player.getLocation().getBlock().getLightFromSky();
        return light >= 14;
    }
}
