package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;

public class NoCoordinatesModifier extends Modifier {

    public NoCoordinatesModifier(RollAndDeathSMP plugin) {
        super(plugin, "Sin Coordenadas", ModifierType.CHAOS, "F3 desactivado/oculto completamente.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.REDUCED_DEBUG_INFO, false);
        }
    }
}
