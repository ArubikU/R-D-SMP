package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class ForcedPvPModifier extends Modifier {

    public ForcedPvPModifier(RollAndDeathSMP plugin) {
        super(plugin, "PvP Forzado", ModifierType.CHAOS, "PvP activado en todas partes por 24h.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for (World world : Bukkit.getWorlds()) {
            world.setPVP(true);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // We don't necessarily want to disable PvP if it was enabled before, 
        // but for safety we might leave it or set to default. 
        // Assuming default is true for SMP, but maybe false in spawn.
        // We'll leave it as is or maybe we should store previous state?
        // For now, let's just leave it enabled or do nothing.
    }
}
