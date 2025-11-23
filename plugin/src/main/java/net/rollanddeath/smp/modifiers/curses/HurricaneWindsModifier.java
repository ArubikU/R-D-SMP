package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class HurricaneWindsModifier extends Modifier {

    public HurricaneWindsModifier(JavaPlugin plugin) {
        super(plugin, "Vientos Huracanados", ModifierType.CURSE, "Empuje (Knockback) recibido es x3.");
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            // Workaround for missing EntityKnockbackEvent in some environments
            Bukkit.getScheduler().runTask(plugin, () -> {
                Vector velocity = player.getVelocity();
                // Check if velocity is likely from knockback (upward and horizontal)
                // This is a heuristic.
                if (velocity.getY() > 0 && (Math.abs(velocity.getX()) > 0 || Math.abs(velocity.getZ()) > 0)) {
                    player.setVelocity(velocity.multiply(3));
                }
            });
        }
    }
}
