package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AnimalFriendshipModifier extends Modifier {

    public AnimalFriendshipModifier(JavaPlugin plugin) {
        super(plugin, "Amistad Animal", ModifierType.BLESSING, "Lobos y Gatos domados son inmortales.");
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Tameable tameable) {
            if (tameable.isTamed() && (tameable instanceof Wolf || tameable instanceof Cat)) {
                event.setCancelled(true);
            }
        }
    }
}
