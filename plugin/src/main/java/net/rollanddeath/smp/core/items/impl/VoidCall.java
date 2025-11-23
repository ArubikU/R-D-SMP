package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class VoidCall extends CustomItem {

    public VoidCall(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.VOID_CALL);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.SCULK_SHRIEKER);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Crea agujero negro temporal.");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("RIGHT")) return;
        ItemStack item = event.getItem();
        if (!isItem(item)) return;

        Player player = event.getPlayer();
        event.setCancelled(true);
        item.setAmount(item.getAmount() - 1);

        Location loc = player.getTargetBlock(null, 50).getLocation().add(0, 1, 0);
        
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 200) { // 10 seconds
                    this.cancel();
                    return;
                }
                
                loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 10, 0.5, 0.5, 0.5, 0);
                
                for (Entity entity : loc.getWorld().getNearbyEntities(loc, 10, 10, 10)) {
                    if (entity instanceof Player && ((Player) entity).getGameMode().name().equals("SPECTATOR")) continue;
                    
                    Vector dir = loc.toVector().subtract(entity.getLocation().toVector()).normalize().multiply(0.5);
                    entity.setVelocity(entity.getVelocity().add(dir));
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }
}
