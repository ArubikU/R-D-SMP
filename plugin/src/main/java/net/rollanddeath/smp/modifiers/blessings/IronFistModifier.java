package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class IronFistModifier extends Modifier {

    public IronFistModifier(JavaPlugin plugin) {
        super(plugin, "Puño de Hierro", ModifierType.BLESSING, "Golpear con la mano hace daño como espada de piedra.");
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                // Base damage is usually 1. We want 5.
                // But modifiers might apply.
                // Simplest way: Set base damage to 5 if it's lower.
                if (event.getDamage() < 5.0) {
                    event.setDamage(5.0);
                }
            }
        }
    }
}
