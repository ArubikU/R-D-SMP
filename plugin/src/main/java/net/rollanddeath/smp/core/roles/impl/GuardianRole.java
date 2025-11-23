package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class GuardianRole extends Role {

    public GuardianRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.GUARDIAN);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 0, false, false));
                        for (Entity nearby : player.getNearbyEntities(5, 5, 5)) {
                            if (nearby instanceof Player ally) {
                                ally.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 0, false, false));
                                ally.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 0, false, false));
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
