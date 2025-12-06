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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PoseidonTrident extends CustomItem {

    private static final long COOLDOWN_MS = 2000L;
    private static final int COOLDOWN_TICKS = 40; // matches 2s
    private final Map<UUID, Long> cooldowns = new HashMap<>();

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
        if (!(event.getEntity() instanceof Trident trident)) return;
        if (!(trident.getShooter() instanceof Player player)) return;
        if (!trident.getScoreboardTags().contains("rd_poseidon_trident")) return;

        if (event.getHitEntity() != null) {
            event.getHitEntity().getWorld().strikeLightning(event.getHitEntity().getLocation());
        } else if (event.getHitBlock() != null) {
            event.getHitBlock().getWorld().strikeLightning(event.getHitBlock().getLocation());
        }

        // Start cooldown after the special hit is processed to avoid rapid lightning spam.
        startCooldown(player);
    }
    
    @EventHandler
    public void onLaunch(org.bukkit.event.entity.ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Trident) || !(event.getEntity().getShooter() instanceof Player player)) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isItem(item)) return;
        if (isOnCooldown(player) || hasVisualCooldown(player)) {
            // Prevent special behavior if on cooldown; let it behave like a normal trident.
            return;
        }
        event.getEntity().addScoreboardTag("rd_poseidon_trident");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("RIGHT")) return;
        ItemStack item = event.getItem();
        if (!isItem(item)) return;

        Player player = event.getPlayer();
        if (isOnCooldown(player) || hasVisualCooldown(player)) {
            event.setCancelled(true);
            return;
        }

        if (!player.isInWater()) {
            // Simulate a dry-riptide dash without locking the player in the riptide state
            Vector direction = player.getLocation().getDirection().normalize();
            player.setVelocity(direction.multiply(2.8));
            startCooldown(player);

            // Grant brief slow-fall and dolphin's grace to smooth landing and feel closer to riptide.
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 30, 0, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 30, 0, false, false, false));

            // Ensure any lingering riptide flag is cleared shortly after use.
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.setRiptiding(false), 10L);
        }
    }

    private void startCooldown(Player player) {
        long expires = System.currentTimeMillis() + COOLDOWN_MS;
        cooldowns.put(player.getUniqueId(), expires);
        player.setCooldown(Material.TRIDENT, COOLDOWN_TICKS);
    }

    private boolean isOnCooldown(Player player) {
        Long expires = cooldowns.get(player.getUniqueId());
        if (expires == null) return false;
        if (System.currentTimeMillis() > expires) {
            cooldowns.remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    private boolean hasVisualCooldown(Player player) {
        return player.hasCooldown(Material.TRIDENT);
    }
}
