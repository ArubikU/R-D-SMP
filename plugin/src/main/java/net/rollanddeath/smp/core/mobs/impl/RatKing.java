package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class RatKing extends CustomMob {

    public RatKing(RollAndDeathSMP plugin) {
        super(plugin, MobType.RAT_KING, EntityType.SILVERFISH);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 60.0);
        setAttackDamage(entity, 6.0);
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getEntity().getScoreboardTags().contains(MobType.RAT_KING.name())) {
            // 20% chance to spawn a minion when hit
            if (Math.random() < 0.2) {
                LivingEntity minion = (LivingEntity) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.SILVERFISH);
                minion.setCustomName("Rat Minion");
                minion.setCustomNameVisible(true);
            }
        }
    }
}
