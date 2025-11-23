package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class SlipperyHandsModifier extends Modifier {

    private final Random random = new Random();

    public SlipperyHandsModifier(JavaPlugin plugin) {
        super(plugin, "Manos Resbaladizas", ModifierType.CURSE, "Chance del 1% de soltar el ítem de tu mano al usarlo.");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK ||
            event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            
            if (!event.hasItem()) return;
            
            if (random.nextInt(100) == 0) { // 1% chance
                Player player = event.getPlayer();
                ItemStack item = event.getItem();
                
                // Drop item
                player.getWorld().dropItemNaturally(player.getLocation(), item);
                
                // Remove from hand
                if (event.getHand() == org.bukkit.inventory.EquipmentSlot.HAND) {
                    player.getInventory().setItemInMainHand(null);
                } else {
                    player.getInventory().setItemInOffHand(null);
                }
                
                event.setCancelled(true);
                player.sendMessage("¡Se te resbaló el objeto!");
            }
        }
    }
}
