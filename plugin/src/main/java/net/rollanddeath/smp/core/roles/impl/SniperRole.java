package net.rollanddeath.smp.core.roles.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

public class SniperRole extends Role {

    public SniperRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.SNIPER);
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player && hasRole(player)) {
            if (event.getProjectile() instanceof Arrow arrow) {
                // +25% velocidad de flecha y sin gravedad para tiros precisos
                arrow.setVelocity(arrow.getVelocity().multiply(1.25));
                arrow.setGravity(false);
            }
        }
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player player) {
            if (hasRole(player)) {
                double distance = player.getLocation().distance(event.getEntity().getLocation());
                if (distance > 12) {
                    // Daño escalado: x1.6 a 12-20 bloques, x2.0 a 20-30 bloques, x2.5 a 30+ bloques
                    double multiplier = distance > 30 ? 2.5 : (distance > 20 ? 2.0 : 1.6);
                    event.setDamage(event.getDamage() * multiplier);
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<green>¡Tiro lejano! Daño aumentado x" + String.format("%.1f", multiplier) + "."));
                    player.sendMessage(MiniMessage.miniMessage().deserialize(
                        "<gray>Distancia: <yellow>" + String.format("%.1f", distance) + " bloques <gray>| Daño: <yellow>" + String.format("%.2f", event.getFinalDamage())
                    ));


                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }
            }
        }
        if (event.getDamager() instanceof Player player && !(event.getDamager() instanceof Arrow)) {
            if (hasRole(player)) {
                event.setDamage(event.getDamage() * 0.85); // -15% melee (reducido de -30%)
            }
        }
    }
}
