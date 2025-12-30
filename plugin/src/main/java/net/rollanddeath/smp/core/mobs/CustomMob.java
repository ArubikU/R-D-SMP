package net.rollanddeath.smp.core.mobs;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;

public abstract class CustomMob implements Listener {

    protected final RollAndDeathSMP plugin;
    protected final String id;
    protected final String displayName;
    protected final EntityType entityType;

    public CustomMob(RollAndDeathSMP plugin, String id, String displayName, EntityType entityType) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = displayName;
        this.entityType = entityType;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public LivingEntity spawn(Location location) {
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, entityType);
        if (displayName != null) {
            entity.customName(MiniMessage.miniMessage().deserialize(displayName));
            entity.setCustomNameVisible(true);
        }
        
        // Tag the entity to identify it as a custom mob
        entity.addScoreboardTag("custom_mob");
        entity.addScoreboardTag(id);

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
