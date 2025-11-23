package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ToxicSunModifier extends Modifier {

    public ToxicSunModifier(JavaPlugin plugin) {
        super(plugin, "Sol Tóxico", ModifierType.CURSE, "La luz directa del sol causa lentitud y hambre.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isExposedToSun(player)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0)); // 2 seconds
                        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 40, 0));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Run every second
    }

    private boolean isExposedToSun(Player player) {
        World world = player.getWorld();
        if (world.getEnvironment() != World.Environment.NORMAL) return false;
        
        long time = world.getTime();
        boolean isDay = time > 0 && time < 12300;
        if (!isDay) return false;

        if (world.hasStorm()) return false;

        return player.getLocation().getBlock().getLightFromSky() == 15;
    }
}
