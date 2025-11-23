package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.entity.Player;

public class NoHungerModifier extends Modifier {

    public NoHungerModifier(RollAndDeathSMP plugin) {
        super(plugin, "Sin Hambre", ModifierType.BLESSING, "La barra de comida no baja si no corres.");
    }

    @Override
    public void onEnable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            // Si el nivel de comida baja (es decir, el nuevo nivel es menor que el actual)
            if (event.getFoodLevel() < player.getFoodLevel()) {
                // Si NO está corriendo, cancelamos la bajada
                if (!player.isSprinting()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
