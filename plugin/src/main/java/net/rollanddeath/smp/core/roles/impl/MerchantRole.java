package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.Material;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class MerchantRole extends Role {

    public MerchantRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.MERCHANT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 140, 2, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 140, 0, false, false));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 80L);
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            if (hasRole(player)) {
                if (Math.random() < 0.12) { // 12% chance
                    int amount = 1 + (int) (Math.random() * 2); // 1-2 emeralds
                    event.getDrops().add(new ItemStack(Material.EMERALD, amount));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player target && hasRole(target)) {
            return; // already targeting merchant
        }
        Player nearbyMerchant = null;
        if (event.getEntity().getWorld() == null) return;
        for (Player player : event.getEntity().getWorld().getPlayers()) {
            if (!hasRole(player)) continue;
            if (player.getLocation().distanceSquared(event.getEntity().getLocation()) <= 144) { // 12 blocks
                nearbyMerchant = player;
                break;
            }
        }
        if (nearbyMerchant != null) {
            event.setTarget(nearbyMerchant);
        }
    }

    @EventHandler
    public void onVillagerTradeRefresh(VillagerAcquireTradeEvent event) {
        AbstractVillager villager = event.getEntity();
        boolean hasNearbyMerchant = villager.getWorld().getPlayers().stream()
            .anyMatch(player -> hasRole(player) && player.getLocation().distanceSquared(villager.getLocation()) <= 144);
        if (!hasNearbyMerchant) return;

        MerchantRecipe recipe = event.getRecipe();
        float adjusted = Math.max(0.05f, recipe.getPriceMultiplier() * 0.75f);
        recipe.setPriceMultiplier(adjusted);
        event.setRecipe(recipe);
    }
}
