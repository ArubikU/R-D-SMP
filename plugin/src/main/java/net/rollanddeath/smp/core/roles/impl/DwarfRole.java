package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class DwarfRole extends Role {

    public DwarfRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.DWARF);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                Attribute scaleAttr = Attribute.SCALE;

                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        if (player.getAttribute(Attribute.MAX_HEALTH).getValue() != 16.0) {
                            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(16.0);
                        }
                        if (player.getAttribute(Attribute.KNOCKBACK_RESISTANCE) != null && player.getAttribute(Attribute.KNOCKBACK_RESISTANCE).getValue() != 0.1) {
                            player.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(0.1);
                        }
                        if (scaleAttr != null && player.getAttribute(scaleAttr) != null) {
                            if (player.getAttribute(scaleAttr).getValue() != 0.75) {
                                player.getAttribute(scaleAttr).setBaseValue(0.75);
                            }
                        }
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 220, 0, false, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 220, 0, false, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 220, 0, false, false, false));
                    } else {
                         if (player.getAttribute(Attribute.MAX_HEALTH).getValue() == 16.0 && 
                            plugin.getRoleManager().getPlayerRole(player) != RoleType.DWARF) {
                            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0);
                        }
                        if (player.getAttribute(Attribute.KNOCKBACK_RESISTANCE) != null && player.getAttribute(Attribute.KNOCKBACK_RESISTANCE).getValue() == 0.1 &&
                                plugin.getRoleManager().getPlayerRole(player) != RoleType.DWARF) {
                            player.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(0.0);
                        }
                        if (scaleAttr != null && player.getAttribute(scaleAttr) != null) {
                            if (player.getAttribute(scaleAttr).getValue() == 0.75 && 
                                plugin.getRoleManager().getPlayerRole(player) != RoleType.DWARF) {
                                player.getAttribute(scaleAttr).setBaseValue(1.0);
                            }
                        }
                        player.removePotionEffect(PotionEffectType.SPEED);
                        player.removePotionEffect(PotionEffectType.STRENGTH);
                        player.removePotionEffect(PotionEffectType.HASTE);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 80L);
    }
}
