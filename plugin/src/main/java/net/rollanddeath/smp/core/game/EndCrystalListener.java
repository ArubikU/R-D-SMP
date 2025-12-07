package net.rollanddeath.smp.core.game;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public class EndCrystalListener implements Listener {

    private final RollAndDeathSMP plugin;
    private final NamespacedKey hitsKey;

    public EndCrystalListener(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.hitsKey = new NamespacedKey(plugin, "crystal_hits");
    }

    @EventHandler
    public void onCrystalDamage(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.ENDER_CRYSTAL) return;
        
        Entity crystal = event.getEntity();
        if (!(crystal instanceof EnderCrystal)) return;

        // Check current hits
        int hits = crystal.getPersistentDataContainer().getOrDefault(hitsKey, PersistentDataType.INTEGER, 0);
        hits++;

        if (hits < 3) {
            event.setCancelled(true);
            crystal.getPersistentDataContainer().set(hitsKey, PersistentDataType.INTEGER, hits);
            
            // Visual/Audio feedback
            crystal.getWorld().playSound(crystal.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 2.0f);
            crystal.getWorld().spawnParticle(org.bukkit.Particle.CRIT, crystal.getLocation(), 10);
            
            // Maybe show a hologram or message? For now just sound/particle is enough feedback that it took a hit but didn't break.
        } else {
            // Allow break (3rd hit)
            // Reset just in case logic changes, but entity will die anyway
        }
    }
}
