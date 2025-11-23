package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.Random;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class RussianRouletteModifier extends Modifier {

    private final Random random = new Random();

    public RussianRouletteModifier(RollAndDeathSMP plugin) {
        super(plugin, "Ruleta Rusa", ModifierType.CHAOS, "Comer tiene 1% de chance de matar.");
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (Math.random() < 1.0 / 6.0) {
            event.getPlayer().setHealth(0);
            event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<red>¡Has perdido la Ruleta Rusa!"));
            plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize("<red>" + event.getPlayer().getName() + " murió jugando a la Ruleta Rusa."));
        }
    }
}
