package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SnowBlindnessModifier extends Modifier {

    private BukkitRunnable task;

    public SnowBlindnessModifier(JavaPlugin plugin) {
        super(plugin, "Ceguera de Nieve", ModifierType.CURSE, "Niebla blanca densa en todas partes (Como Powder Snow).");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setFreezeTicks(150); // Keeps the frost effect
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null) task.cancel();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setFreezeTicks(0);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FREEZE) {
            event.setCancelled(true);
        }
    }
}
