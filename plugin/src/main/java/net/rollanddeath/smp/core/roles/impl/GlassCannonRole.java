package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GlassCannonRole extends Role {

    public GlassCannonRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.GLASS_CANNON);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        if (player.getAttribute(Attribute.MAX_HEALTH).getBaseValue() != 6.0) {
                            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(6.0); // 3 hearts
                            if (player.getHealth() > 6.0) {
                                player.setHealth(6.0);
                            }
                        }
                    } else if (player.getAttribute(Attribute.MAX_HEALTH).getBaseValue() == 6.0 &&
                            plugin.getRoleManager().getPlayerRole(player) != RoleType.GLASS_CANNON) {
                        player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 80L);
    }

    @EventHandler
    public void onDealDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (hasRole(player)) {
                event.setDamage(event.getDamage() * 2.0);
            }
        }
    }

    @EventHandler
    public void onTakeDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (hasRole(player)) {
                event.setDamage(event.getDamage() * 2.0);
            }
        }
    }
}
