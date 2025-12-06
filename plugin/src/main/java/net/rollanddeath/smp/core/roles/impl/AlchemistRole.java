package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;

public class AlchemistRole extends Role {

    public AlchemistRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.ALCHEMIST);
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player player && hasRole(player)) {
            if (event.getAction() == EntityPotionEffectEvent.Action.ADDED || event.getAction() == EntityPotionEffectEvent.Action.CHANGED) {
                PotionEffect old = event.getNewEffect();
                if (old != null && old.getDuration() > 20) {
                    if (event.getCause() == EntityPotionEffectEvent.Cause.POTION_DRINK || event.getCause() == EntityPotionEffectEvent.Cause.POTION_SPLASH) {
                         event.setCancelled(true);
                         player.addPotionEffect(new PotionEffect(old.getType(), old.getDuration() * 3, old.getAmplifier(), old.isAmbient(), old.hasParticles(), old.hasIcon()));
                    }
                }
            }
        }
    }
}
