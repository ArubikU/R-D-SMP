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
        if (!player.isInWater() && !player.isRiptiding()) {
            // Simulate Riptide
            Vector direction = player.getLocation().getDirection();
            player.setVelocity(direction.multiply(3));
            player.setRiptiding(true); // This might not work if not in water/rain in vanilla logic, but API might allow it.
            // If setRiptiding doesn't work visually or logically without water, we just launch them.
        }
    }
}
