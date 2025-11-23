package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class EngineerRole extends Role {

    public EngineerRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.ENGINEER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100, 1, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 100, 1, false, false));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 80L);
    }
}
