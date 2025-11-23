package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;
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
                Attribute scaleAttr;
                try {
                    scaleAttr = Attribute.valueOf("GENERIC_SCALE");
                } catch (IllegalArgumentException e) {
                    scaleAttr = null;
                }

                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() != 40.0) {
                            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40.0);
                        }
                        if (scaleAttr != null && player.getAttribute(scaleAttr) != null) {
                            if (player.getAttribute(scaleAttr).getValue() != 2.0) {
                                player.getAttribute(scaleAttr).setBaseValue(2.0);
                            }
                        }
                    } else {
                         if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() == 40.0 && 
                            plugin.getRoleManager().getPlayerRole(player) != RoleType.GIANT) {
                            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
                        }
                        if (scaleAttr != null && player.getAttribute(scaleAttr) != null) {
                            if (player.getAttribute(scaleAttr).getValue() == 2.0 && 
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
}
