package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class TankRole extends Role {

    public TankRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.TANK);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 0, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1, false, false));
                        
                        if (player.getAttribute(Attribute.MAX_HEALTH).getValue() != 40.0) {
                            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(40.0);
                        }
                    } else {
                        // Reset health if they lost the role (simple check)
                        // Ideally RoleManager handles cleanup when switching roles
                        if (player.getAttribute(Attribute.MAX_HEALTH).getValue() == 40.0 && 
                            plugin.getRoleManager().getPlayerRole(player) != RoleType.TANK) {
                            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 80L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!hasRole(player)) return;
        if (event.getFoodLevel() < player.getFoodLevel()) {
            int diff = player.getFoodLevel() - event.getFoodLevel();
            event.setFoodLevel(player.getFoodLevel() - (diff * 3)); // hambre x3
        }
    }
}
