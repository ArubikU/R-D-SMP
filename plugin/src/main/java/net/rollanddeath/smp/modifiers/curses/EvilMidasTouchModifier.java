package net.rollanddeath.smp.modifiers.curses;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class EvilMidasTouchModifier extends Modifier {

    public EvilMidasTouchModifier(JavaPlugin plugin) {
        super(plugin, "Toque de Midas Maligno", ModifierType.CURSE, "La comida se convierte en oro (no comestible) al tocarla.");
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack item = event.getItem().getItemStack();
            if (item.getType().isEdible() && item.getType() != Material.GOLDEN_APPLE && item.getType() != Material.ENCHANTED_GOLDEN_APPLE) {
                event.setCancelled(true);
                event.getItem().remove();
                
                ItemStack gold = new ItemStack(Material.GOLD_INGOT, item.getAmount());
                player.getInventory().addItem(gold).forEach((k, v) -> player.getWorld().dropItem(player.getLocation(), v));
                
                player.sendMessage(Component.text("¡Tu comida se ha convertido en oro!", NamedTextColor.GOLD));
            }
        }
    }
}
