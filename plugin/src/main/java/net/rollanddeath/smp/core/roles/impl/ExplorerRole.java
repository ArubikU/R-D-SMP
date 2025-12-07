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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
                        PlayerInventory inv = player.getInventory();
                        for (int slot = 27; slot <= 35; slot++) { // restringir última fila
                            ItemStack item = inv.getItem(slot);
                            if (item == null || item.getType() == Material.AIR) {
                                continue;
                            }

                            inv.setItem(slot, null);

                            ItemStack remaining = item.clone();
                            if (!storeInAllowedSlots(inv, remaining)) {
                                player.getWorld().dropItemNaturally(player.getLocation(), remaining);
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

    private boolean storeInAllowedSlots(PlayerInventory inv, ItemStack stack) {
        // First try to merge with existing stacks outside the blocked row (0-26)
        for (int slot = 0; slot <= 26 && stack.getAmount() > 0; slot++) {
            ItemStack existing = inv.getItem(slot);
            if (existing == null || existing.getType() == Material.AIR) continue;
            if (!existing.isSimilar(stack)) continue;

            int max = existing.getMaxStackSize();
            int space = max - existing.getAmount();
            if (space <= 0) continue;

            int move = Math.min(space, stack.getAmount());
            existing.setAmount(existing.getAmount() + move);
            stack.setAmount(stack.getAmount() - move);
        }

        // Then place into empty allowed slots (0-26)
        for (int slot = 0; slot <= 26 && stack.getAmount() > 0; slot++) {
            ItemStack existing = inv.getItem(slot);
            if (existing == null || existing.getType() == Material.AIR) {
                inv.setItem(slot, stack.clone());
                stack.setAmount(0);
                break;
            }
        }

        return stack.getAmount() == 0;
    }
}
