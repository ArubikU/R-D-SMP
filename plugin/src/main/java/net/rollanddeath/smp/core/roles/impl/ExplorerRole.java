package net.rollanddeath.smp.core.roles.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ExplorerRole extends Role {

    public ExplorerRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.EXPLORER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0, false, false));
                        for (int slot = 27; slot <= 35; slot++) { // restringir última fila
                            var item = player.getInventory().getItem(slot);
                            if (item != null) {
                                var leftover = player.getInventory().addItem(item.clone());
                                player.getInventory().setItem(slot, null);
                                if (!leftover.isEmpty()) {
                                    leftover.values().forEach(stack -> player.getWorld().dropItemNaturally(player.getLocation(), stack));
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player && hasRole(player)) {
            if (player.isSprinting()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!hasRole(player)) return;
        if (event.getClickedInventory() != null && event.getClickedInventory().equals(player.getInventory())) {
            if (event.getSlot() >= 27 && event.getSlot() <= 35) {
                event.setCancelled(true);
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Tienes menos espacio de inventario."));
            }
        }
    }
}
