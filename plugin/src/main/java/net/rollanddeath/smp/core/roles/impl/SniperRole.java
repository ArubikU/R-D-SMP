package net.rollanddeath.smp.core.roles.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SniperRole extends Role {

    public SniperRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.SNIPER);
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player player) {
            if (hasRole(player)) {
                double distance = player.getLocation().distance(event.getEntity().getLocation());
                if (distance > 20) {
                    event.setDamage(event.getDamage() * 1.5);
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<green>¡Tiro lejano! Daño aumentado."));
                }
            }
        }
    }
}
