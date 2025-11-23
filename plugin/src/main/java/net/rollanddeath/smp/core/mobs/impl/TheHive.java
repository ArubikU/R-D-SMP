package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

public class TheHive extends CustomMob {

    public TheHive(RollAndDeathSMP plugin) {
        super(plugin, MobType.THE_HIVE, EntityType.BEE);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 30.0);
        setAttackDamage(entity, 6.0);
        if (entity instanceof Bee) {
            ((Bee) entity).setAnger(Integer.MAX_VALUE);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity().getScoreboardTags().contains(MobType.THE_HIVE.name())) {
            // Spawn 3 normal bees
            for (int i = 0; i < 3; i++) {
                Bee bee = (Bee) event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.BEE);
                bee.setAnger(Integer.MAX_VALUE);
            }
        }
    }
}
