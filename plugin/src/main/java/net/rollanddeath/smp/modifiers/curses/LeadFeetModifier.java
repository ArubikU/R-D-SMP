package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class LeadFeetModifier extends Modifier {

    public LeadFeetModifier(JavaPlugin plugin) {
        super(plugin, "Pies de Plomo", ModifierType.CURSE, "No puedes nadar hacia arriba, te hundes rápido.");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().isInWater()) {
            // Apply downward velocity if trying to go up or just constantly
            // If player is swimming up, counteract it.
            if (event.getTo().getY() > event.getFrom().getY()) {
                Vector velocity = event.getPlayer().getVelocity();
                velocity.setY(velocity.getY() - 0.1); // Pull down
                event.getPlayer().setVelocity(velocity);
            }
        }
    }
}
