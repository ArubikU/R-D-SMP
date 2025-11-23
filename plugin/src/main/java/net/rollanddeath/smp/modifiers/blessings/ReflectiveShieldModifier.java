package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ReflectiveShieldModifier extends Modifier {

    public ReflectiveShieldModifier(JavaPlugin plugin) {
        super(plugin, "Escudo Reflejante", ModifierType.BLESSING, "Bloquear devuelve el 50% del daño.");
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player && event.getDamager() instanceof LivingEntity attacker) {
            if (player.isBlocking()) {
                // Simple check: if player is blocking, reflect 50% of incoming damage
                double damage = event.getDamage();
                attacker.damage(damage * 0.5, player);
            }
        }
    }
}
