package net.rollanddeath.smp.core.mobs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;

public abstract class CustomMob implements Listener {

    protected final RollAndDeathSMP plugin;
    protected final MobType type;
    protected final EntityType entityType;

    public CustomMob(RollAndDeathSMP plugin, MobType type, EntityType entityType) {
        this.plugin = plugin;
        this.type = type;
        this.entityType = entityType;
    }

    public MobType getType() {
        return type;
    }

    public LivingEntity spawn(Location location) {
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);
        entity.customName(MiniMessage.miniMessage().deserialize(type.getDisplayName()));
        entity.setCustomNameVisible(true);
        
        // Tag the entity to identify it as a custom mob
        entity.addScoreboardTag("custom_mob");
        entity.addScoreboardTag(type.name());

        applyAttributes(entity);
        applyEquipment(entity);
        
        return entity;
    }

    protected abstract void applyAttributes(LivingEntity entity);
    
    protected void applyEquipment(LivingEntity entity) {
        // Default implementation: no equipment
    }
    
    protected void setMaxHealth(LivingEntity entity, double health) {
        if (entity.getAttribute(Attribute.MAX_HEALTH) != null) {
            entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
            entity.setHealth(health);
        }
    }
    
    protected void setAttackDamage(LivingEntity entity, double damage) {
        if (entity.getAttribute(Attribute.ATTACK_DAMAGE) != null) {
            entity.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(damage);
        }
    }
    
    protected void setMovementSpeed(LivingEntity entity, double speed) {
        if (entity.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
            entity.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(speed);
        }
    }
}
