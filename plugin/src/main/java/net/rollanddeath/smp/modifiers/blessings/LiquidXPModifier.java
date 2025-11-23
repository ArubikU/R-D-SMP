package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class LiquidXPModifier extends Modifier {

    public LiquidXPModifier(JavaPlugin plugin) {
        super(plugin, "Experiencia Líquida", ModifierType.BLESSING, "Todo da x3 XP.");
    }

    @EventHandler
    public void onExpChange(PlayerExpChangeEvent event) {
        event.setAmount(event.getAmount() * 3);
    }
}
