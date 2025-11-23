package net.rollanddeath.smp.core.mobs.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.MobType;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

public class WetCreeper extends CustomMob {

    public WetCreeper(RollAndDeathSMP plugin) {
        super(plugin, MobType.WET_CREEPER, EntityType.CREEPER);
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        setMaxHealth(entity, 20.0);
        if (entity instanceof Creeper) {
            ((Creeper) entity).setMaxFuseTicks(20); // Explodes faster
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (event.getEntity().getScoreboardTags().contains(MobType.WET_CREEPER.name())) {
            event.getLocation().getBlock().setType(Material.WATER);
        }
    }
}
