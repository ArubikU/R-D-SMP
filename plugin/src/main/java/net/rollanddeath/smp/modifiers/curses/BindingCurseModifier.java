package net.rollanddeath.smp.modifiers.curses;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

public class BindingCurseModifier extends Modifier {

    public BindingCurseModifier(JavaPlugin plugin) {
        super(plugin, "Maldición de Binding", ModifierType.CURSE, "Toda armadura equipada no se puede quitar.");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(Component.text("¡No puedes quitarte la armadura!", NamedTextColor.RED));
        }
    }
}
