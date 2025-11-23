package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Tag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class RottenWoodModifier extends Modifier {

    private final Random random = new Random();

    public RottenWoodModifier(JavaPlugin plugin) {
        super(plugin, "Madera Podrida", ModifierType.CURSE, "Talar madera tiene chance de no dropear nada.");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Tag.LOGS.isTagged(event.getBlock().getType())) {
            if (random.nextBoolean()) { // 50% chance (adjust as needed, description says "chance")
                event.setDropItems(false);
            }
        }
    }
}
