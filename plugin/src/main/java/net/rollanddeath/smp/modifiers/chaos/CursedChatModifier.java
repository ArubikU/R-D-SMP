package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class CursedChatModifier extends Modifier {

    public CursedChatModifier(RollAndDeathSMP plugin) {
        super(plugin, "Chat Maldito", ModifierType.CHAOS, "Hablar en chat te hace daño.");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (Math.random() < 0.3) {
            event.getPlayer().damage(2.0);
            event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<red>¡El chat está maldito! Te duele hablar."));
        }
    }
}
