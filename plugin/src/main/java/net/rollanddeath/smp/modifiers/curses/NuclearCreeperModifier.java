package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NuclearCreeperModifier extends Modifier {

    public NuclearCreeperModifier(JavaPlugin plugin) {
        super(plugin, "Creeper Nuclear", ModifierType.CURSE, "Explosiones de Creeper son x3 más grandes.");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onCreeperPrime(ExplosionPrimeEvent event) {
        if (event.getEntityType() == EntityType.CREEPER) {
            // Default radius is 3. x3 = 9.
            // Charged creepers are 6. x3 = 18.
            float newRadius = event.getRadius() * 3;
            event.setRadius(newRadius);
        }
    }
}
