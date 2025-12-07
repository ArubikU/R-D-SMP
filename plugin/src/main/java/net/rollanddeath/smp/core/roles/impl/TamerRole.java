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
                    double incoming = event.getDamage();
                    double share = incoming * 0.5; // mitad se transfiere al dueño
                    event.setDamage(incoming * 0.5); // la mascota recibe solo la mitad
                    owner.damage(share, event.getDamager());
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onOwnerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!hasRole(player)) return;

        double reduction = calculatePetReduction(player);
        if (reduction <= 0) return;

        event.setDamage(event.getDamage() * (1.0 - reduction));
    }

    private double calculatePetReduction(Player player) {
        // Diminishing 2%, 1.9%, 1.805%, ... capped at 50%
        double reduction = 0.0;
        int index = 0;

        for (var entity : player.getWorld().getNearbyEntities(player.getLocation(), 12, 12, 12)) {
            if (!(entity instanceof Tameable pet)) continue;
            if (!pet.isTamed()) continue;
            if (!(pet.getOwner() instanceof Player owner)) continue;
            if (!owner.getUniqueId().equals(player.getUniqueId())) continue;

            double contrib = 0.02 * Math.pow(0.95, index); // diminishing per pet
            reduction += contrib;
            index++;
            if (reduction >= 0.5) {
                reduction = 0.5;
                break;
            }
        }

        return Math.min(0.5, reduction);
    }
}
