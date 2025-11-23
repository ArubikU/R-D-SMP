package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class DenseAtmosphereModifier extends Modifier {

    public DenseAtmosphereModifier(JavaPlugin plugin) {
        super(plugin, "Atmósfera Densa", ModifierType.CURSE, "Las elytras se rompen más rápido y los cohetes se consumen el doble.");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onElytraDamage(PlayerItemDamageEvent event) {
        if (event.getItem().getType() == Material.ELYTRA) {
            event.setDamage(event.getDamage() * 2);
        }
    }

    @EventHandler
    public void onRocketUse(PlayerInteractEvent event) {
        if (!event.hasItem()) return;
        if (event.getItem().getType() != Material.FIREWORK_ROCKET) return;
        
        Player player = event.getPlayer();
        if (player.isGliding()) {
            // Consume an extra rocket if possible
            ItemStack hand = event.getItem();
            if (hand.getAmount() > 1) {
                hand.setAmount(hand.getAmount() - 1);
            } else {
                player.getInventory().removeItem(new ItemStack(Material.FIREWORK_ROCKET, 1));
            }
        }
    }
}
