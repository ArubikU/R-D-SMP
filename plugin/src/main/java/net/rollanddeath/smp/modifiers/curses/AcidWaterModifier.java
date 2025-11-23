package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AcidWaterModifier extends Modifier {

    public AcidWaterModifier(JavaPlugin plugin) {
        super(plugin, "Agua Ácida", ModifierType.CURSE, "Entrar al agua aplica Veneno.");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        // Optimization: Check only if block changed
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && 
            event.getFrom().getBlockY() == event.getTo().getBlockY() && 
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        if (player.isInWater()) {
            // Apply Poison I for 5 seconds (100 ticks)
            // If already has poison, we might want to extend it or leave it.
            // addPotionEffect overwrites if duration is longer or amplifier is higher/same?
            // Actually force=true ensures it applies.
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0));
        } else {
            // Check if standing in rain? The description says "Entrar al agua".
            // Rain logic is separate usually.
        }
    }
}
