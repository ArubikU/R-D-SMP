package net.rollanddeath.smp.core.items;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

public class CraftingListener implements Listener {

    private final RollAndDeathSMP plugin;

    public CraftingListener(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;
        ItemStack result = event.getRecipe().getResult();
        
        CustomItem item = getCustomItemFromStack(result);
        if (item == null) return;

        Player player = (Player) event.getView().getPlayer();

        if (item.getType() == CustomItemType.RESURRECTION_ORB) {
            player.sendMessage(Component.text("⚠ ADVERTENCIA: Craftear este ítem te costará 1 VIDA.", NamedTextColor.RED));
        } else if (item.getType() == CustomItemType.SOUL_CONTRACT) {
            player.sendMessage(Component.text("⚠ ADVERTENCIA: Craftear este ítem te BANEARÁ por 24 HORAS.", NamedTextColor.DARK_RED));
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.getRecipe() == null) return;
        ItemStack result = event.getRecipe().getResult();
        
        CustomItem item = getCustomItemFromStack(result);
        if (item == null) return;

        Player player = (Player) event.getWhoClicked();

        if (item.getType() == CustomItemType.RESURRECTION_ORB) {
            int lives = plugin.getLifeManager().getLives(player);
            if (lives <= 1) {
                event.setCancelled(true);
                player.sendMessage(Component.text("No tienes suficientes vidas para craftear esto. Morirías permanentemente.", NamedTextColor.RED));
                return;
            }
            
            plugin.getLifeManager().removeLife(player);
            player.sendMessage(Component.text("Has sacrificado 1 vida para crear el Orbe de Resurrección.", NamedTextColor.RED));
        } 
        else if (item.getType() == CustomItemType.SOUL_CONTRACT) {
            // Ban logic
            Date expiration = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000); // 24h
            player.ban("Has firmado el Contrato de Alma. Nos vemos en 24 horas.", expiration, "RollAndDeath SMP");
            // Kick is handled by ban() usually, but if not, the ban prevents re-login.
            // We won't kick explicitly to avoid double-kick errors if ban does it.
            // If ban doesn't kick, we might need to, but standard behavior is kick.
            if (player.isOnline()) {
                 player.kick(Component.text("Has firmado el Contrato de Alma.\n\nNos vemos en 24 horas.", NamedTextColor.DARK_RED));
            }
        }
    }

    private CustomItem getCustomItemFromStack(ItemStack stack) {
        for (CustomItemType type : CustomItemType.values()) {
            CustomItem item = plugin.getItemManager().getItem(type);
            if (item != null && item.getItemStack().isSimilar(stack)) {
                return item;
            }
        }
        return null;
    }
}
