package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.DyeColor;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Zombie;

final class SetEntityPropertyAction {
    private SetEntityPropertyAction() {}

    static void register() {
        ActionRegistrar.register("set_entity_property", SetEntityPropertyAction::parse, "set_property", "modify_entity");
    }

    private static Action parse(Map<?, ?> raw) {
        Object targetSpec = raw.get("target");
        if (targetSpec == null) targetSpec = raw.get("entity");
        
        String property = Resolvers.string(null, raw, "property", "prop", "attribute");
        Object valueSpec = raw.get("value");
        if (valueSpec == null) valueSpec = raw.get("set");
        
        if (property == null) return null;

        return ctx -> {
            List<Entity> targets = Resolvers.entities(ctx, targetSpec);
            if (targets.isEmpty()) {
                if (targetSpec == null && ctx.subject() != null) {
                    targets = List.of(ctx.subject());
                } else {
                    return ActionResult.ALLOW;
                }
            }

            Object val = Resolvers.resolve(ctx, valueSpec);

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity e : targets) {
                    applyProperty(e, property, val);
                }
            });

            return ActionResult.ALLOW;
        };
    }

    private static void applyProperty(Entity e, String prop, Object val) {
        switch (prop.toLowerCase()) {
            case "silent":
                e.setSilent(toBool(val, false));
                break;
            case "glowing":
                e.setGlowing(toBool(val, false));
                break;
            case "gravity":
                e.setGravity(toBool(val, true));
                break;
            case "invulnerable":
                e.setInvulnerable(toBool(val, false));
                break;
            case "custom_name":
                e.setCustomName(String.valueOf(val));
                e.setCustomNameVisible(true);
                break;
            case "custom_name_visible":
                e.setCustomNameVisible(toBool(val, true));
                break;
            case "fire_ticks":
                e.setFireTicks(toInt(val, 0));
                break;
            case "freeze_ticks":
                e.setFreezeTicks(toInt(val, 0));
                break;
            case "visual_fire":
                e.setVisualFire(toBool(val, false));
                break;
            
            // Mob specific
            case "baby":
                if (e instanceof Zombie z) z.setBaby(toBool(val, true));
                // Add other ageables if needed
                break;
            case "size":
                if (e instanceof Slime s) s.setSize(toInt(val, 1));
                if (e instanceof Phantom p) p.setSize(toInt(val, 1));
                break;
            case "anger":
                if (e instanceof Bee b) b.setAnger(toInt(val, 0));
                break;
            case "fuse_ticks":
            case "max_fuse_ticks":
                if (e instanceof Creeper c) c.setMaxFuseTicks(toInt(val, 30));
                break;
            case "powered":
                if (e instanceof Creeper c) c.setPowered(toBool(val, true));
                break;
            case "charging":
                if (e instanceof Vex v) v.setCharging(toBool(val, true));
                break;
            case "color":
                if (e instanceof Shulker s) {
                    try {
                        s.setColor(DyeColor.valueOf(String.valueOf(val).toUpperCase()));
                    } catch (Exception ignored) {}
                }
                break;
            case "player_created":
                if (e instanceof IronGolem ig) ig.setPlayerCreated(toBool(val, true));
                break;
            case "ai":
                if (e instanceof LivingEntity le) le.setAI(toBool(val, true));
                break;
            case "collidable":
                if (e instanceof LivingEntity le) le.setCollidable(toBool(val, true));
                break;
            case "gliding":
                if (e instanceof LivingEntity le) le.setGliding(toBool(val, true));
                break;
            case "swimming":
                if (e instanceof LivingEntity le) le.setSwimming(toBool(val, true));
                break;
            case "invisible":
                if (e instanceof LivingEntity le) le.setInvisible(toBool(val, true));
                break;
        }
    }

    private static boolean toBool(Object val, boolean def) {
        if (val instanceof Boolean b) return b;
        if (val instanceof String s) return Boolean.parseBoolean(s);
        if (val instanceof Number n) return n.intValue() != 0;
        return def;
    }

    private static int toInt(Object val, int def) {
        if (val instanceof Number n) return n.intValue();
        if (val instanceof String s) {
            try {
                return Integer.parseInt(s);
            } catch (Exception ignored) {}
        }
        return def;
    }
}
