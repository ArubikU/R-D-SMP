package net.rollanddeath.smp.core.roles.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IllusionistRole extends Role {

    public IllusionistRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.ILLUSIONIST);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && hasRole(player)) {
            if (Math.random() < 0.25) { // 25% chance to dodge/illusion
                event.setCancelled(true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0, false, false));
                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 20, 0.5, 1, 0.5, 0.1);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (!hasRole(player)) return;
        String name = event.getItem().getType().name();
        if (name.contains("BEEF") || name.contains("PORK") || name.contains("CHICKEN") || name.contains("MUTTON") || name.contains("RABBIT") || name.contains("COD") || name.contains("SALMON") || name.contains("FISH") || name.contains("FLESH")) {
            event.setCancelled(true);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No puedes comer carne."));
        }
    }
}
