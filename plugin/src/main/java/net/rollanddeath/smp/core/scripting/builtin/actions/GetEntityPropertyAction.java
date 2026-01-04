package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Zombie;

final class GetEntityPropertyAction {
    private GetEntityPropertyAction() {}

    static void register() {
        ActionRegistrar.register("get_entity_property", GetEntityPropertyAction::parse, 
            "get_property", "read_entity",
            "get_slime_size_to_var"
        );
    }

    private static Action parse(Map<?, ?> raw) {
        Object targetSpec = raw.get("target");
        if (targetSpec == null) targetSpec = raw.get("entity");
        
        String type = String.valueOf(raw.get("type"));
        
        String property = Resolvers.string(null, raw, "property", "prop", "attribute");
        String storeKey = Resolvers.string(null, raw, "store", "var", "key");
        
        // Handle aliases
        if ("get_slime_size_to_var".equalsIgnoreCase(type)) {
            property = "size";
            if (targetSpec == null) targetSpec = "EVENT.entity";
            if (storeKey == null) storeKey = Resolvers.string(null, raw, "key");
        }

        if (property == null || storeKey == null) return null;

        final String finalProp = property.toLowerCase();
        final Object finalTargetSpec = targetSpec;
        final String finalStoreKey = storeKey;

        return ctx -> {
            List<Entity> targets = Resolvers.entities(ctx, finalTargetSpec);
            if (targets.isEmpty()) {
                if (finalTargetSpec == null && ctx.subject() != null) {
                    targets = List.of(ctx.subject());
                } else {
                    return ActionResult.ALLOW;
                }
            }

            // Only get from first target
            Entity e = targets.get(0);
            Object result = getProperty(e, finalProp);
            
            if (result != null) {
                ctx.setGenericVarCompat(finalStoreKey, result);
            }

            return ActionResult.ALLOW;
        };
    }

    private static Object getProperty(Entity e, String prop) {
        switch (prop) {
            case "silent": return e.isSilent();
            case "glowing": return e.isGlowing();
            case "gravity": return e.hasGravity();
            case "invulnerable": return e.isInvulnerable();
            case "custom_name": return e.getCustomName();
            case "custom_name_visible": return e.isCustomNameVisible();
            case "fire_ticks": return e.getFireTicks();
            case "freeze_ticks": return e.getFreezeTicks();
            case "visual_fire": return e.isVisualFire();
            case "uuid": return e.getUniqueId().toString();
            case "type": return e.getType().name();
            case "name": return e.getName();
            
            // Mob specific
            case "baby":
                if (e instanceof Zombie z) return z.isBaby();
                if (e instanceof Ageable a) return !a.isAdult();
                return null;
            case "age":
                if (e instanceof Ageable a) return a.getAge();
                return null;
            case "size":
                if (e instanceof Slime s) return s.getSize();
                if (e instanceof Phantom p) return p.getSize();
                return null;
            case "anger":
                if (e instanceof Bee b) return b.getAnger();
                return null;
            case "fuse_ticks":
                if (e instanceof Creeper c) return c.getMaxFuseTicks(); // Usually we want current fuse? getFuseTicks()
                return null;
            case "max_fuse_ticks":
                if (e instanceof Creeper c) return c.getMaxFuseTicks();
                return null;
            case "powered":
                if (e instanceof Creeper c) return c.isPowered();
                return null;
            case "charging":
                if (e instanceof Vex v) return v.isCharging();
                return null;
            case "color":
                if (e instanceof Shulker s) return s.getColor() != null ? s.getColor().name() : "PURPLE";
                return null;
            case "player_created":
                if (e instanceof IronGolem ig) return ig.isPlayerCreated();
                return null;
            case "ai":
                if (e instanceof LivingEntity le) return le.hasAI();
                return null;
            case "collidable":
                if (e instanceof LivingEntity le) return le.isCollidable();
                return null;
            case "gliding":
                if (e instanceof LivingEntity le) return le.isGliding();
                return null;
            case "swimming":
                if (e instanceof LivingEntity le) return le.isSwimming();
                return null;
            case "invisible":
                if (e instanceof LivingEntity le) return le.isInvisible();
                return null;
            case "health":
                if (e instanceof LivingEntity le) return le.getHealth();
                return null;
            case "max_health":
                if (e instanceof LivingEntity le) return le.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
                return null;
            case "owner":
                if (e instanceof Tameable t && t.getOwner() != null) return t.getOwner().getName();
                return null;
        }
        return null;
    }
}
