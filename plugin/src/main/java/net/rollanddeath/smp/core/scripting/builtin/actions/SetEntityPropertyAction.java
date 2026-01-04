package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.DyeColor;
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

final class SetEntityPropertyAction {
    private SetEntityPropertyAction() {}

    static void register() {
        ActionRegistrar.register("set_entity_property", SetEntityPropertyAction::parse, 
            "set_property", "modify_entity",
            "grow_ageable_to_max", 
            "set_event_entity_silent", 
            "set_slime_size",
            "set_fire_ticks"
        );
    }

    private static Action parse(Map<?, ?> raw) {
        Object targetSpec = raw.get("target");
        if (targetSpec == null) targetSpec = raw.get("entity");
        
        String type = String.valueOf(raw.get("type"));
        
        String property = Resolvers.string(null, raw, "property", "prop", "attribute");
        Object valueSpec = raw.get("value");
        if (valueSpec == null) valueSpec = raw.get("set");
        
        // Handle aliases
        if ("grow_ageable_to_max".equalsIgnoreCase(type)) {
            property = "age";
            valueSpec = "max";
            if (targetSpec == null) targetSpec = "EVENT.block"; // Try block first (crops), fallback to entity
        } else if ("set_event_entity_silent".equalsIgnoreCase(type)) {
            property = "silent";
            if (targetSpec == null) targetSpec = "EVENT.entity";
        } else if ("set_slime_size".equalsIgnoreCase(type)) {
            property = "size";
            valueSpec = raw.get("size");
            if (targetSpec == null) targetSpec = "EVENT.entity";
        } else if ("set_fire_ticks".equalsIgnoreCase(type)) {
            property = "fire_ticks";
            valueSpec = raw.get("ticks");
            if (valueSpec == null) valueSpec = raw.get("duration");
        }

        if (property == null) return null;

        final String finalProp = property.toLowerCase();
        final Object finalValueSpec = valueSpec;
        final Object finalTargetSpec = targetSpec;

        return ctx -> {
            List<Entity> targets = Resolvers.entities(ctx, finalTargetSpec);
            
            // Special handling for crops (Ageable BlockData) if targets is empty
            if (targets.isEmpty() && "age".equals(finalProp)) {
                org.bukkit.Location loc = Resolvers.location(ctx, finalTargetSpec);
                if (loc == null && ctx.location() != null) loc = ctx.location();
                
                if (loc != null) {
                    final org.bukkit.Location finalLoc = loc;
                    ActionUtils.runSync(ctx.plugin(), () -> {
                        org.bukkit.block.Block b = finalLoc.getBlock();
                        if (b.getBlockData() instanceof org.bukkit.block.data.Ageable ageable) {
                            if ("max".equalsIgnoreCase(String.valueOf(finalValueSpec)) || "adult".equalsIgnoreCase(String.valueOf(finalValueSpec))) {
                                ageable.setAge(ageable.getMaximumAge());
                            } else {
                                Integer age = Resolvers.integer(ctx, finalValueSpec);
                                if (age != null) ageable.setAge(Math.min(age, ageable.getMaximumAge()));
                            }
                            b.setBlockData(ageable);
                        }
                    });
                    return ActionResult.ALLOW;
                }
            }

            if (targets.isEmpty()) {
                if (finalTargetSpec == null && ctx.subject() != null) {
                    targets = List.of(ctx.subject());
                } else {
                    return ActionResult.ALLOW;
                }
            }

            Object val = Resolvers.resolve(ctx, finalValueSpec);

            final List<Entity> finalTargets = targets;
            final Object finalVal = val;

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity e : finalTargets) {
                    applyProperty(e, finalProp, finalVal);
                }
            });

            return ActionResult.ALLOW;
        };
    }

    private static void applyProperty(Entity e, String prop, Object val) {
        switch (prop) {
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
                if (e instanceof Ageable a) {
                    if (toBool(val, true)) a.setBaby();
                    else a.setAdult();
                }
                break;
            case "age":
                if (e instanceof Ageable a) {
                    if ("max".equalsIgnoreCase(String.valueOf(val)) || "adult".equalsIgnoreCase(String.valueOf(val))) {
                        a.setAdult();
                    } else if ("baby".equalsIgnoreCase(String.valueOf(val))) {
                        a.setBaby();
                    } else {
                        a.setAge(toInt(val, 0));
                    }
                } else if (e instanceof Zombie z) {
                     if ("baby".equalsIgnoreCase(String.valueOf(val))) z.setBaby(true);
                     else z.setBaby(false);
                }
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
            case "owner":
                if (e instanceof Tameable t) {
                    Player owner = toPlayer(val, e);
                    if (owner != null) t.setOwner(owner);
                }
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
    
    private static Player toPlayer(Object val, Entity e) {
        if (val instanceof Player p) return p;
        if (val instanceof String s) {
            return e.getServer().getPlayerExact(s);
        }
        return null;
    }
}
