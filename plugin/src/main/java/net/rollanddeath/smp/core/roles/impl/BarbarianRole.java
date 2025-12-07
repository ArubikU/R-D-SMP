package net.rollanddeath.smp.core.roles.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;

public class BarbarianRole extends Role {

    public BarbarianRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.BARBARIAN);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && hasRole(player)) {
            event.setDamage(event.getDamage() * 1.5); // +50% damage
        }
    }

    @EventHandler
    public void onEquip(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player && hasRole(player)) {
            if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
                ItemStack item = event.getCursor();
                if (isForbiddenArmor(item)) {
                    event.setCancelled(true);
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>¡Los bárbaros no usan armaduras de netherite!"));
                }
            }
        }
    }

    private boolean isForbiddenArmor(ItemStack item) {
        if (item == null) return false;
        String name = item.getType().name();
        return name.contains("NETHERITE");
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnchant(PrepareItemEnchantEvent event) {
        if (event.getEnchanter() instanceof Player player && hasRole(player)) {
            event.setCancelled(true);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Los bárbaros no pueden encantar objetos."));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {
        if (hasRole(event.getEnchanter())) {
            event.setCancelled(true);
            event.getEnchanter().sendMessage(MiniMessage.miniMessage().deserialize("<red>Los bárbaros no pueden encantar objetos."));
        }
    }
}
