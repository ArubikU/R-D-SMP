package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NoMilkModifier extends Modifier {

    public NoMilkModifier(JavaPlugin plugin) {
        super(plugin, "Sin Leche", ModifierType.CURSE, "No se puede beber leche.");
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.MILK_BUCKET) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("No puedes beber leche.", NamedTextColor.RED));
        }
    }
}
