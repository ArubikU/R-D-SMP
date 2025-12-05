package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class PoseidonTrident extends CustomItem {

    public PoseidonTrident(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.POSEIDON_TRIDENT);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.TRIDENT);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Invoca rayos sin tormenta. Riptide fuera del agua.");
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Trident) {
            Trident trident = (Trident) event.getEntity();
            if (trident.getShooter() instanceof Player) {
                // We can't easily check if the trident item itself is ours because it's an entity now.
                // But we can check if the player held it when shooting? 
                // Or check PDC on the trident entity if we transferred it.
                // For simplicity, let's assume if the player has the item in inventory (loyalty) or we tag it on launch.
                // Wait, we can tag it on launch.
                if (trident.getScoreboardTags().contains("rd_poseidon_trident")) {
                    if (event.getHitEntity() != null) {
                        event.getHitEntity().getWorld().strikeLightning(event.getHitEntity().getLocation());
                    } else if (event.getHitBlock() != null) {
                        event.getHitBlock().getWorld().strikeLightning(event.getHitBlock().getLocation());
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onLaunch(org.bukkit.event.entity.ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Trident && event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (isItem(item)) {
                event.getEntity().addScoreboardTag("rd_poseidon_trident");
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("RIGHT")) return;
        ItemStack item = event.getItem();
        if (!isItem(item)) return;
        
        Player player = event.getPlayer();
        if (!player.isInWater()) {
            // Simulate a dry-riptide dash without locking the player in the riptide state
            Vector direction = player.getLocation().getDirection().normalize();
            player.setVelocity(direction.multiply(2.8));
            player.setCooldown(Material.TRIDENT, 20); // prevent spam and avoids stuck attack cooldowns

            // Grant brief slow-fall and dolphin's grace to smooth landing and feel closer to riptide.
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 30, 0, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 30, 0, false, false, false));

            // Ensure any lingering riptide flag is cleared shortly after use.
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.setRiptiding(false), 10L);
        }
    }
}
