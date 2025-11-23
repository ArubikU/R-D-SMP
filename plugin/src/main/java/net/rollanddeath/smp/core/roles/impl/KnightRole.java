package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class KnightRole extends Role {

    public KnightRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.KNIGHT);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (hasRole(player)) {
                Material item = player.getInventory().getItemInMainHand().getType();
                if (item.name().contains("SWORD")) {
                    event.setDamage(event.getDamage() * 1.3); // +30% damage
                }
            }
        }
        
        if (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player player) {
             if (hasRole(player)) {
                 event.setDamage(event.getDamage() * 0.5); // -50% bow damage
             }
        }
    }
}
