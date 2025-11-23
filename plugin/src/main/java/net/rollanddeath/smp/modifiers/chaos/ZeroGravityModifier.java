package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ZeroGravityModifier extends Modifier {

    private BukkitRunnable task;

    public ZeroGravityModifier(RollAndDeathSMP plugin) {
        super(plugin, "Gravedad Cero", ModifierType.CHAOS, "Todos tienen levitación leve constante.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 0, false, false));
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 400L);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null) {
            task.cancel();
            task = null;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.removePotionEffect(PotionEffectType.LEVITATION);
        }
    }
}
