package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CorruptedGolem extends CustomMob {

    public CorruptedGolem(RollAndDeathSMP plugin) {
        super(plugin, MobType.CORRUPTED_GOLEM, EntityType.IRON_GOLEM);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 150.0);
        setAttackDamage(entity, 15.0);
        setMovementSpeed(entity, 0.2);
        
        if (entity instanceof IronGolem) {
            ((IronGolem) entity).setPlayerCreated(false);
        }
    }
    
    @Override
    public LivingEntity spawn(org.bukkit.Location location) {
        LivingEntity entity = super.spawn(location);
        if (entity instanceof IronGolem) {
            // Try to target nearest player
            Player nearest = null;
            double distance = Double.MAX_VALUE;
            for (Player p : location.getWorld().getPlayers()) {
                double d = p.getLocation().distanceSquared(location);
                if (d < distance && d < 400) { // 20 blocks
                    distance = d;
                    nearest = p;
                }
            }
            if (nearest != null) {
                ((IronGolem) entity).setTarget(nearest);
            }
        }
        return entity;
    }
}
