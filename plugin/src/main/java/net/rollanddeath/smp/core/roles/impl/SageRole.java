package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class SageRole extends Role {

    public SageRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.SAGE);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (var player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0, false, false));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 80L);
    }

    @EventHandler
    public void onExp(PlayerExpChangeEvent event) {
        if (hasRole(event.getPlayer())) {
            event.setAmount(event.getAmount() * 2);
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 0, false, false));
        }
    }
}
