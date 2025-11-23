package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class VampireBloodModifier extends Modifier {

    public VampireBloodModifier(JavaPlugin plugin) {
        super(plugin, "Sangre de Vampiro", ModifierType.BLESSING, "Matar mobs cura medio corazón.");
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player player) {
            double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            double newHealth = Math.min(player.getHealth() + 1.0, maxHealth);
            player.setHealth(newHealth);
        }
    }
}
