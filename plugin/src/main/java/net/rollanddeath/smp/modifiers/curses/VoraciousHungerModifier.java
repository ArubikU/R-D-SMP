package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class VoraciousHungerModifier extends Modifier {

    public VoraciousHungerModifier(JavaPlugin plugin) {
        super(plugin, "Hambre Voraz", ModifierType.CURSE, "El hambre baja 3 veces más rápido.");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        int oldLevel = event.getEntity().getFoodLevel();
        int newLevel = event.getFoodLevel();
        
        if (newLevel < oldLevel) {
            int diff = oldLevel - newLevel;
            event.setFoodLevel(Math.max(0, oldLevel - (diff * 3)));
        }
    }
}
