package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NoEnchantsModifier extends Modifier {

    public NoEnchantsModifier(JavaPlugin plugin) {
        super(plugin, "Sin Encantamientos", ModifierType.CURSE, "No se pueden encantar objetos ni usar yunques.");
    }

    @EventHandler
    public void onEnchant(PrepareItemEnchantEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent event) {
        event.setResult(null);
    }
}
