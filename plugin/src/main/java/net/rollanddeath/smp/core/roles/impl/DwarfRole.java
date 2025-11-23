package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
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
                Attribute scaleAttr;
                try {
                    scaleAttr = Attribute.valueOf("SCALE");
                } catch (IllegalArgumentException e) {
                    scaleAttr = null;
                }

                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        if (player.getAttribute(Attribute.MAX_HEALTH).getValue() != 16.0) {
                            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(16.0);
                        }
                        if (scaleAttr != null && player.getAttribute(scaleAttr) != null) {
                            if (player.getAttribute(scaleAttr).getValue() != 0.5) {
                                player.getAttribute(scaleAttr).setBaseValue(0.5);
                            }
                        }
                    } else {
                         if (player.getAttribute(Attribute.MAX_HEALTH).getValue() == 16.0 && 
                            plugin.getRoleManager().getPlayerRole(player) != RoleType.DWARF) {
                            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0);
                        }
                        if (scaleAttr != null && player.getAttribute(scaleAttr) != null) {
                            if (player.getAttribute(scaleAttr).getValue() == 0.5 && 
                                plugin.getRoleManager().getPlayerRole(player) != RoleType.DWARF) {
                                player.getAttribute(scaleAttr).setBaseValue(1.0);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 80L);
    }
}
