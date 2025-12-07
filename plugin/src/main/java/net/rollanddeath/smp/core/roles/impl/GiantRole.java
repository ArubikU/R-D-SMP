package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GiantRole extends Role {

    public GiantRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.GIANT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                Attribute scaleAttr = Attribute.SCALE;
                Attribute kbAttr = Attribute.KNOCKBACK_RESISTANCE;

                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        if (player.getAttribute(Attribute.MAX_HEALTH).getValue() != 24.0) {
                            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(24.0);
                        }
                        if (kbAttr != null && player.getAttribute(kbAttr) != null) {
                            if (player.getAttribute(kbAttr).getValue() != 0.35) {
                                player.getAttribute(kbAttr).setBaseValue(0.35);
                            }
                        }
                        if (scaleAttr != null && player.getAttribute(scaleAttr) != null) {
                            if (player.getAttribute(scaleAttr).getValue() != 1.25) {
                                player.getAttribute(scaleAttr).setBaseValue(1.25);
                            }
                        }
                    } else {
                         if (player.getAttribute(Attribute.MAX_HEALTH).getValue() == 24.0 && 
                            plugin.getRoleManager().getPlayerRole(player) != RoleType.GIANT) {
                            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0);
                        }
                        if (kbAttr != null && player.getAttribute(kbAttr) != null) {
                            if (player.getAttribute(kbAttr).getValue() == 0.35 &&
                                plugin.getRoleManager().getPlayerRole(player) != RoleType.GIANT) {
                                player.getAttribute(kbAttr).setBaseValue(0.0);
                            }
                        }
                        if (scaleAttr != null && player.getAttribute(scaleAttr) != null) {
                            if (player.getAttribute(scaleAttr).getValue() == 1.25 && 
                                plugin.getRoleManager().getPlayerRole(player) != RoleType.GIANT) {
                                player.getAttribute(scaleAttr).setBaseValue(1.0);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 80L);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player && hasRole(player)) {
            // Hunger drains faster
            if (event.getFoodLevel() < player.getFoodLevel()) {
                int diff = player.getFoodLevel() - event.getFoodLevel();
                event.setFoodLevel(player.getFoodLevel() - (diff * 2));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFall(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!hasRole(player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        double damage = event.getDamage();
        event.setDamage(damage * 0.75); // softer landings

        for (Entity nearby : player.getNearbyEntities(3.0, 2.0, 3.0)) {
            if (nearby instanceof LivingEntity living && nearby != player) {
                double splash = Math.max(2.0, damage * 0.5);
                living.damage(splash, player);
            }
        }
    }
}
