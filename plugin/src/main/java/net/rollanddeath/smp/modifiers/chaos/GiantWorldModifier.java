package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class GiantWorldModifier extends Modifier {

    public GiantWorldModifier(RollAndDeathSMP plugin) {
        super(plugin, "Mundo Gigante", ModifierType.CHAOS, "Los Slimes y Magma Cubes son x4 tamaño.");
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Slime slime) {
            slime.setSize(slime.getSize() * 4);
        } else if (event.getEntity() instanceof MagmaCube magma) {
            magma.setSize(magma.getSize() * 4);
        }
    }
}
