package net.rollanddeath.smp.core.roles.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.block.Biome;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class DruidRole extends Role {

    public DruidRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.DRUID);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        // Forest Buffs
                        Biome biome = player.getLocation().getBlock().getBiome();
                        String biomeName = biome.getKey().getKey();
                        if (biomeName.contains("FOREST") || biomeName.contains("JUNGLE") || biomeName.contains("TAIGA")) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0, false, false));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0, false, false));
                        }

                        // Attract Animals
                        for (Entity entity : player.getNearbyEntities(10, 5, 10)) {
                            if (entity instanceof Animals animal) {
                                animal.setTarget(player);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 60L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEquip(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!hasRole(player)) return;
        if (event.getSlotType() != InventoryType.SlotType.ARMOR) return;
        if (event.getCursor() == null) return;
        String name = event.getCursor().getType().name();
        if (name.contains("IRON") || name.contains("GOLD") || name.contains("CHAIN") || name.contains("NETHERITE")) {
            event.setCancelled(true);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Los druidas no usan armadura de metal."));
        }
    }
}
