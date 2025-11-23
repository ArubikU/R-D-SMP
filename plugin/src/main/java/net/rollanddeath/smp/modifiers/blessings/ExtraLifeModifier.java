package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ExtraLifeModifier extends Modifier {

    private BukkitRunnable task;

    public ExtraLifeModifier(RollAndDeathSMP plugin) {
        super(plugin, "Vida Extra", ModifierType.BLESSING, "+1 Corazón Dorado (absorción) que se regenera cada día.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                long time = Bukkit.getWorlds().get(0).getTime();
                // Check if it's morning (0 to 200 ticks)
                if (time >= 0 && time < 200) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        // Apply Absorption I (2 hearts)
                        // Duration: Infinite (until lost)
                        // If they already have it, this refreshes it (healing the absorption hearts)
                        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, PotionEffect.INFINITE_DURATION, 0, false, false));
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 100L); // Check every 5 seconds
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
