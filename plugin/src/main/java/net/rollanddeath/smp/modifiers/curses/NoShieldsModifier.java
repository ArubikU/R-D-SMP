package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NoShieldsModifier extends Modifier {

    public NoShieldsModifier(JavaPlugin plugin) {
        super(plugin, "Sin Escudos", ModifierType.CURSE, "El uso de escudos está prohibido.");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getType() == Material.SHIELD) {
                event.setCancelled(true);
                // Optional: Send message "Shields are disabled!"
            }
            // Also check offhand
            if (event.getPlayer().getInventory().getItemInOffHand().getType() == Material.SHIELD) {
                event.setCancelled(true);
            }
        }
    }
}
