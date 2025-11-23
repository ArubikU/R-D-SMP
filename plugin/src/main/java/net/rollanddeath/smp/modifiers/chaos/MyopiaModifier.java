package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MyopiaModifier extends Modifier {

    public MyopiaModifier(RollAndDeathSMP plugin) {
        super(plugin, "Miopes", ModifierType.CHAOS, "Distancia de renderizado forzada a 2 chunks.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setViewDistance(2);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setViewDistance(10); // Reset to default (or server default)
        }
    }
}
