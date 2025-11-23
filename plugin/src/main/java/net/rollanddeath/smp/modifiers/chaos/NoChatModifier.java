package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.event.EventHandler;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class NoChatModifier extends Modifier {

    public NoChatModifier(RollAndDeathSMP plugin) {
        super(plugin, "Sin Chat", ModifierType.CHAOS, "El chat está deshabilitado. Nadie puede leerte.");
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<red>El chat está deshabilitado por el evento 'Sin Chat'."));
    }
}
