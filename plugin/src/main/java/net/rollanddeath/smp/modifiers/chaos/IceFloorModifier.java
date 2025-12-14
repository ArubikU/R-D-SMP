package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class IceFloorModifier extends Modifier {

    private BukkitRunnable task;

    public IceFloorModifier(RollAndDeathSMP plugin) {
        super(plugin, "Suelo de Hielo", ModifierType.CHAOS, "Todo el mundo resbala como en hielo.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // Simulating slippery floor with Speed III
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 2, false, false));
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null) {
            task.cancel();
            task = null;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            PotionEffect speed = player.getPotionEffect(PotionEffectType.SPEED);
            if (speed != null && speed.getAmplifier() == 2) {
                player.removePotionEffect(PotionEffectType.SPEED);
            }
        }
    }
}
