package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class DeepBlindnessModifier extends Modifier {

    private BukkitTask task;

    public DeepBlindnessModifier(JavaPlugin plugin) {
        super(plugin, "Ceguera Profunda", ModifierType.CURSE, "Bajo Y=0, tienes Ceguera permanente.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::checkPlayers, 20L, 20L); // Check every second
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    private void checkPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().getY() < 0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0, false, false));
            }
        }
    }
}
