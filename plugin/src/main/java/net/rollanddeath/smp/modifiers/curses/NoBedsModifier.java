package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NoBedsModifier extends Modifier {

    public NoBedsModifier(JavaPlugin plugin) {
        super(plugin, "Sin Camas", ModifierType.CURSE, "Las camas explotan al intentar dormir.");
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        event.setCancelled(true);
        event.getBed().getWorld().createExplosion(event.getBed().getLocation(), 5.0F, true, true);
    }
}
