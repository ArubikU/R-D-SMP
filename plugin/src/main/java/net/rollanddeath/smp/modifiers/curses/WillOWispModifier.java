package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WillOWispModifier extends Modifier {

    public WillOWispModifier(JavaPlugin plugin) {
        super(plugin, "Fuego Fatuo", ModifierType.CURSE, "El fuego azul (soul fire) mata instantáneamente.");
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                Block block = player.getLocation().getBlock();
                if (block.getType() == Material.SOUL_FIRE) {
                    player.setHealth(0);
                }
            }
        }
    }
}
