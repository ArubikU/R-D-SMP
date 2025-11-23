package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IceCreeper extends CustomMob {

    public IceCreeper(RollAndDeathSMP plugin) {
        super(plugin, MobType.ICE_CREEPER, EntityType.CREEPER);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 20.0);
        setMovementSpeed(entity, 0.25);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (event.getEntity().getScoreboardTags().contains(MobType.ICE_CREEPER.name())) {
            event.setCancelled(true); // Cancel block damage
            event.getLocation().createExplosion(3.0f, false, false); // Visual explosion
            
            // Freeze area
            int radius = 4;
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        Block block = event.getLocation().clone().add(x, y, z).getBlock();
                        if (block.getType() == Material.AIR && block.getLocation().distance(event.getLocation()) <= radius) {
                            // Chance to create ice/snow
                            if (Math.random() < 0.3) {
                                block.setType(Material.POWDER_SNOW);
                            }
                        } else if (block.getType() == Material.WATER) {
                            block.setType(Material.ICE);
                        }
                    }
                }
            }
            
            // Slow nearby entities
            event.getLocation().getNearbyEntities(5, 5, 5).forEach(e -> {
                if (e instanceof LivingEntity) {
                    ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 2));
                }
            });
        }
    }
}
