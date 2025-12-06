package net.rollanddeath.smp.core.roles.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

public class BerserkerRole extends Role {

    public BerserkerRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.BERSERKER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        ItemStack off = player.getInventory().getItemInOffHand();
                        if (off.getType() == Material.SHIELD) {
                            player.getInventory().setItemInOffHand(null);
                            player.getWorld().dropItemNaturally(player.getLocation(), off);
                            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No puedes usar escudos."));
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 40L);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (hasRole(player)) {
                double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
                double currentHealth = player.getHealth();
                double missingHealth = maxHealth - currentHealth;

                // +10% damage for every 2 hearts (4 HP) missing
                double multiplier = 1.0 + (missingHealth / 4.0) * 0.1;
                event.setDamage(event.getDamage() * multiplier);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onShieldUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!hasRole(player)) return;
        if (event.getHand() == EquipmentSlot.OFF_HAND && player.getInventory().getItemInOffHand().getType() == Material.SHIELD) {
            event.setCancelled(true);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No puedes usar escudos."));
        }
    }
}
