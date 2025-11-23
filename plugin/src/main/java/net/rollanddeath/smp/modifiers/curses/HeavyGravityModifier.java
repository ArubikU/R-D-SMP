package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;

import org.bukkit.attribute.Attribute;
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
                    //use attribute modifier instead of potion effect to avoid particles
                    p.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(0.25);
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
