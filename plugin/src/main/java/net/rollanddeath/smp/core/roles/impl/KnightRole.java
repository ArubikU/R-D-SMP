package net.rollanddeath.smp.core.roles.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class KnightRole extends Role {

    public KnightRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.KNIGHT);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (hasRole(player)) {
                Material item = player.getInventory().getItemInMainHand().getType();
                if (item.name().contains("SWORD")) {
                    event.setDamage(event.getDamage() * 1.3); // +30% damage
                }
            }
        }
        
        if (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player player) {
             if (hasRole(player)) {
                 event.setDamage(event.getDamage() * 0.5); // -50% bow damage
             }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player && hasRole(player)) {
            event.setCancelled(true);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No puedes usar arcos ni ballestas."));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTridentUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!hasRole(player)) return;
        Material main = player.getInventory().getItemInMainHand().getType();
        if (main == Material.TRIDENT) {
            event.setCancelled(true);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No puedes usar tridentes."));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockHit(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!hasRole(player)) return;
        if (player.isBlocking()) {
            event.setDamage(event.getDamage() * 0.85); // 15% mitigation while usando escudo
        }
    }
}
