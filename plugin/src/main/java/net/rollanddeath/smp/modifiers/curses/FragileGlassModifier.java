package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FragileGlassModifier extends Modifier {

    public FragileGlassModifier(JavaPlugin plugin) {
        super(plugin, "Cristal Frágil", ModifierType.CURSE, "Romper cristal causa explosión pequeña.");
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        if (type.name().contains("GLASS")) {
            event.getBlock().getWorld().createExplosion(event.getBlock().getLocation(), 2.0F, false, false);
        }
    }
}
