package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SnowWarModifier extends Modifier {

    public SnowWarModifier(RollAndDeathSMP plugin) {
        super(plugin, "Guerra de Nieve", ModifierType.CHAOS, "Las bolas de nieve hacen 2 corazones de daño.");
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Snowball) {
            event.setDamage(4.0); // 2 hearts
        }
    }
}
