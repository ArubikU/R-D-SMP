package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class NoArmorModifier extends Modifier {

    public NoArmorModifier(RollAndDeathSMP plugin) {
        super(plugin, "Sin Armadura", ModifierType.CHAOS, "No se puede equipar pecheras hoy.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            checkArmor(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            ItemStack item = event.getCursor();
            if (item != null && item.getType().name().contains("CHESTPLATE")) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(MiniMessage.miniMessage().deserialize("<red>¡No puedes equipar pecheras hoy!"));
            }
        }
        // Also check shift-click
        if (event.isShiftClick()) {
            ItemStack item = event.getCurrentItem();
            if (item != null && item.getType().name().contains("CHESTPLATE")) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(MiniMessage.miniMessage().deserialize("<red>¡No puedes equipar pecheras hoy!"));
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        checkArmor(event.getPlayer());
    }

    private void checkArmor(Player player) {
        ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate != null && chestplate.getType() != Material.AIR) {
            player.getInventory().setChestplate(null);
            player.getInventory().addItem(chestplate);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Tu pechera ha sido desequipada por el evento."));
        }
    }
}
