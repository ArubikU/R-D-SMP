package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class SageRole extends Role {

    public SageRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.SAGE);
    }

    @EventHandler
    public void onExp(PlayerExpChangeEvent event) {
        if (hasRole(event.getPlayer())) {
            event.setAmount(event.getAmount() * 2);
        }
    }
}
