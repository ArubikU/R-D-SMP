package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class BerserkerRole extends Role {

    public BerserkerRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.BERSERKER);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (hasRole(player)) {
                double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
                double currentHealth = player.getHealth();
                double missingHealth = maxHealth - currentHealth;

                // +10% damage for every 2 hearts (4 HP) missing
                double multiplier = 1.0 + (missingHealth / 4.0) * 0.1;
                event.setDamage(event.getDamage() * multiplier);
            }
        }
    }
}
