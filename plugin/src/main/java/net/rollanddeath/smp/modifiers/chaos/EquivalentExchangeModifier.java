package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class EquivalentExchangeModifier extends Modifier {

    public EquivalentExchangeModifier(RollAndDeathSMP plugin) {
        super(plugin, "Intercambio Equivalente", ModifierType.CHAOS, "Matar un mob te teletransporta a su lugar.");
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            killer.teleport(event.getEntity().getLocation());
        }
    }
}
