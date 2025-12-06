package net.rollanddeath.smp.core.roles.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MidasRole extends Role {

    public MidasRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.MIDAS);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        consumeGold(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 12000L, 12000L); // Every 10 minutes (12000 ticks)
    }

    private void consumeGold(Player player) {
        if (player.getInventory().contains(Material.GOLD_NUGGET)) {
            removeItem(player, Material.GOLD_NUGGET, 1);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Has consumido 1 Pepita de Oro para sobrevivir."));
        } else if (player.getInventory().contains(Material.GOLD_INGOT)) {
            removeItem(player, Material.GOLD_INGOT, 1);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Has consumido 1 Lingote de Oro para sobrevivir."));
        } else {
            player.damage(4.0);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>¡Necesitas oro para vivir! Te debilitas..."));
        }
    }

    private void removeItem(Player player, Material type, int amount) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == type) {
                int current = item.getAmount();
                if (current >= amount) {
                    item.setAmount(current - amount);
                    return;
                } else {
                    item.setAmount(0);
                    amount -= current;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onNaturalRegen(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!hasRole(player)) return;
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            event.setCancelled(true); // sin regeneración natural
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (hasRole(player)) {
                ItemStack item = event.getItem().getItemStack();
                if (item.getType() == Material.IRON_INGOT || item.getType() == Material.COPPER_INGOT) {
                    event.getItem().setItemStack(new ItemStack(Material.GOLD_INGOT, item.getAmount()));
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>¡Tu toque convierte el metal en oro!"));
                } else if (item.getType() == Material.COBBLESTONE || item.getType() == Material.STONE) {
                    if (Math.random() < 0.1) {
                        event.getItem().setItemStack(new ItemStack(Material.GOLD_NUGGET, item.getAmount()));
                    }
                }
            }
        }
    }
}
