package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HeavyGravityModifier extends Modifier {

    public HeavyGravityModifier(JavaPlugin plugin) {
        super(plugin, "Gravedad Pesada", ModifierType.CURSE, "No se puede saltar bloques completos. Caída hace x2 daño.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        // Apply Jump Boost 128 (No Jump) constantly
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    // Amplifier 128 prevents jumping in most versions, or negative jump boost
                    // In 1.20+, negative amplifiers work. -5 is usually enough to prevent 1 block jump.
                    // 128 is the old trick. Let's try 250 (byte -6) or 128.
                    // Actually, PotionEffectType.JUMP with amplifier 128 prevents jumping.
                    p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 40, 128, false, false, false));
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setDamage(event.getDamage() * 2);
        }
    }
}
