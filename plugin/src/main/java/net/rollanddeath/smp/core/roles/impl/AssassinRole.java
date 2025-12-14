package net.rollanddeath.smp.core.roles.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class AssassinRole extends Role {

    public AssassinRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.ASSASSIN);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (hasRole(player)) {
            if (event.isSneaking()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 11, false, false));
            } else {
                if (player.hasPotionEffect(PotionEffectType.INVISIBILITY) &&
                    player.getPotionEffect(PotionEffectType.INVISIBILITY).getAmplifier() == 11) {
                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
                }
            }
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && event.getEntity() instanceof LivingEntity target) {
            if (hasRole(player)) {
                Vector playerDir = player.getLocation().getDirection();
                Vector targetDir = target.getLocation().getDirection();
                
                // Check if facing same direction (dot product > 0)
                // Actually, dot product of directions. If > 0.5 roughly same direction.
                if (playerDir.dot(targetDir) > 0.5) {
                    event.setDamage(event.getDamage() * 2.0);
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>¡Puñalada por la espalda! <bold>CRÍTICO"));
                    
                    // Reveal assassin sin tocar invisibilidad de otras fuentes
                    PotionEffect invis = player.getPotionEffect(PotionEffectType.INVISIBILITY);
                    if (invis != null && invis.getAmplifier() == 11) {
                        player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    }
                }
            }
        }
    }
}
