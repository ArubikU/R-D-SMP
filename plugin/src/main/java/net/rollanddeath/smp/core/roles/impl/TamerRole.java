package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class TamerRole extends Role {

    public TamerRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.TAMER);
    }

    @EventHandler
    public void onPetAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Tameable pet) {
            if (pet.isTamed() && pet.getOwner() instanceof Player owner) {
                if (hasRole(owner)) {
                    event.setDamage(event.getDamage() * 2.0);
                }
            }
        }
    }
    
    @EventHandler
    public void onPetDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Tameable pet) {
             if (pet.isTamed() && pet.getOwner() instanceof Player owner) {
                if (hasRole(owner)) {
                    event.setDamage(event.getDamage() * 0.5);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onOwnerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!hasRole(player)) return;
        double damage = event.getDamage() * 0.5; // comparte parte del daño con mascotas
        player.getWorld().getNearbyEntities(player.getLocation(), 12, 12, 12).stream()
                .filter(e -> e instanceof Tameable)
                .map(Tameable.class::cast)
                .filter(Tameable::isTamed)
                .filter(pet -> pet.getOwner() instanceof Player && pet.getOwner().getUniqueId().equals(player.getUniqueId()))
                .forEach(pet -> pet.damage(damage, player));
    }
}
