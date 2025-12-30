package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptPhase;
import net.rollanddeath.smp.core.scripting.projectiles.ScriptedProjectileService;
import net.rollanddeath.smp.core.teams.Team;
import net.rollanddeath.smp.core.teams.TeamManager;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

final class LaunchCurvedProjectileAction {
    private LaunchCurvedProjectileAction() {}

    static void register() {
        ActionRegistrar.register("launch_curved_projectile", LaunchCurvedProjectileAction::parse, "curved_projectile", "scripted_projectile");
    }

    private static Action parse(Map<?, ?> raw) {
        String projectileType = Resolvers.string(null, raw, "projectile_type", "type");
        String shooterKey = Resolvers.string(null, raw, "shooter_key", "shooter");
        String startKey = Resolvers.string(null, raw, "start_key", "start", "origin");
        String targetKey = Resolvers.string(null, raw, "target_key", "target");
        
        Integer durationTicks = Resolvers.integer(null, raw, "duration_ticks", "duration");
        String durationTicksKey = Resolvers.string(null, raw, "duration_ticks_key");
        Double speed = Resolvers.doubleVal(null, raw, "speed");
        String speedKey = Resolvers.string(null, raw, "speed_key");
        
        boolean homing = raw.get("homing") instanceof Boolean b ? b : false;
        
        Double targetYOffset = Resolvers.doubleVal(null, raw, "target_y_offset");
        String targetYOffsetKey = Resolvers.string(null, raw, "target_y_offset_key");
        Double curveHeight = Resolvers.doubleVal(null, raw, "curve_height");
        String curveHeightKey = Resolvers.string(null, raw, "curve_height_key");
        Double curveSide = Resolvers.doubleVal(null, raw, "curve_side");
        String curveSideKey = Resolvers.string(null, raw, "curve_side_key");
        
        boolean explodeOnImpact = raw.get("explode_on_impact") instanceof Boolean b ? b : false;
        boolean explodeOnFinish = raw.get("explode_on_finish") instanceof Boolean b ? b : false;
        Double explosionPower = Resolvers.doubleVal(null, raw, "explosion_power");
        String explosionPowerKey = Resolvers.string(null, raw, "explosion_power_key");
        boolean explosionFire = raw.get("explosion_fire") instanceof Boolean b ? b : false;
        boolean explosionBreakBlocks = raw.get("explosion_break_blocks") instanceof Boolean b ? b : false;
        
        List<Action> onHit = Resolvers.parseActionList(raw.get("on_hit"));
        List<Action> onFinish = Resolvers.parseActionList(raw.get("on_finish"));
        List<Action> onTick = Resolvers.parseActionList(raw.get("on_tick"));
        List<Action> onLaunch = Resolvers.parseActionList(raw.get("on_launch"));
        
        Object targetsSpec = raw.get("targets");

        return ctx -> {
            var plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            ScriptedProjectileService svc = plugin.getScriptedProjectileService();
            if (svc == null) return ActionResult.ALLOW;

            // Resolve targets spec
            List<String> allowedTypes = new java.util.ArrayList<>();
            List<UUID> allowedUuids = new java.util.ArrayList<>();
            
            if (targetsSpec instanceof List<?> list) {
                for (Object o : list) {
                    if (o instanceof String s) allowedTypes.add(s);
                }
            } else if (targetsSpec instanceof String s) {
                allowedTypes.add(s);
            }
            
            // Try to resolve as entities (selectors, uuids)
            List<Entity> resolvedEntities = Resolvers.entities(ctx, targetsSpec);
            for (Entity e : resolvedEntities) {
                allowedUuids.add(e.getUniqueId());
            }

            Object shooterObj = null;
            if (shooterKey != null && !shooterKey.isBlank()) {
                shooterObj = ctx.getValue(shooterKey);
            }
            if (shooterObj == null) {
                shooterObj = ctx.getValue("SUBJECT");
            }
            LivingEntity shooter = shooterObj instanceof LivingEntity le ? le : ctx.player();
            if (shooter == null) return ActionResult.ALLOW;

            Location start = null;
            if (startKey != null && !startKey.isBlank()) {
                start = Resolvers.location(null, ctx.getValue(startKey), null);
            }
            if (start == null) {
                try {
                    start = shooter.getEyeLocation();
                } catch (Exception ignored) {
                    start = shooter.getLocation();
                }
            }
            if (start == null || start.getWorld() == null) return ActionResult.ALLOW;

            Object targetObj = null;
            if (targetKey != null && !targetKey.isBlank()) {
                targetObj = ctx.getValue(targetKey);
            }
            if (targetObj == null) {
                targetObj = ctx.getValue("TARGET");
            }
            
            TeamManager teamManager = plugin.getTeamManager();
            if (targetObj instanceof Entity e && !isValidTarget(e, shooter, teamManager, allowedTypes, allowedUuids)) {
                targetObj = null;
            }

            if (targetObj == null) {
                targetObj = findNearestTarget(start, 48.0, shooter, teamManager, allowedTypes, allowedUuids);
            }

            UUID targetId = null;
            Location end = null;
            if (targetObj instanceof Entity e) {
                targetId = e.getUniqueId();
                end = e.getLocation();
            } else {
                end = Resolvers.location(null, targetObj, null);
            }
            
            if (end == null) {
                try {
                    end = start.clone().add(shooter.getEyeLocation().getDirection().multiply(50));
                } catch (Exception ignored) {
                }
            }

            double resolvedTargetYOffset = resolveDoubleFromLiteralOrKey(ctx, targetYOffset, targetYOffsetKey, 1.0);
            double resolvedCurveHeight = resolveDoubleFromLiteralOrKey(ctx, curveHeight, curveHeightKey, 3.0);
            double resolvedCurveSide = resolveDoubleFromLiteralOrKey(ctx, curveSide, curveSideKey, 0.0);
            double resolvedExplosionPower = resolveDoubleFromLiteralOrKey(ctx, explosionPower, explosionPowerKey, 2.0);

            if (targetObj instanceof Entity) {
                if (end != null) end = end.clone().add(0, resolvedTargetYOffset, 0);
            }

            if (end == null || end.getWorld() == null) return ActionResult.ALLOW;
            if (end.getWorld() != start.getWorld()) return ActionResult.ALLOW;

            Integer resolvedDurationTicks = durationTicks;
            if (resolvedDurationTicks == null && durationTicksKey != null) {
                Double d = Resolvers.resolveDouble(ctx.getValue(durationTicksKey));
                if (d != null) resolvedDurationTicks = (int) Math.round(d);
            }
            
            Double resolvedSpeed = speed;
            if (resolvedSpeed == null && speedKey != null) {
                resolvedSpeed = Resolvers.resolveDouble(ctx.getValue(speedKey));
            }

            int ticks;
            if (resolvedDurationTicks != null && resolvedDurationTicks > 0) {
                ticks = resolvedDurationTicks;
            } else if (resolvedSpeed != null && resolvedSpeed > 0) {
                double dist = Math.max(0.1, start.distance(end));
                ticks = (int) Math.max(5, Math.min(200, Math.round((dist / resolvedSpeed) * 20.0)));
            } else {
                ticks = 40;
            }

            Class<? extends Projectile> projClass = projectileClassFromName(projectileType);
            if (projClass == null) return ActionResult.ALLOW;

            ScriptedProjectileService.ScriptInvocation inv = new ScriptedProjectileService.ScriptInvocation(
                ctx.subjectId(),
                ctx.phase() != null ? ctx.phase() : ScriptPhase.MOB,
                ctx.player(),
                ctx.variables() != null ? new HashMap<>(ctx.variables()) : null
            );

            ScriptedProjectileService.LaunchRequest req = new ScriptedProjectileService.LaunchRequest(
                projClass,
                shooter,
                start,
                end,
                targetId,
                resolvedTargetYOffset,
                homing,
                ticks,
                resolvedCurveHeight,
                resolvedCurveSide,
                null,
                null,
                explodeOnImpact,
                explodeOnFinish,
                resolvedExplosionPower,
                explosionFire,
                explosionBreakBlocks,
                onHit,
                onFinish,
                onTick,
                onLaunch,
                allowedTypes,
                inv
            );

            svc.launchCurvedProjectile(req);
            return ActionResult.ALLOW;
        };
    }

    private static double resolveDoubleFromLiteralOrKey(ScriptContext ctx, Double literal, String key, double fallback) {
        if (literal != null) return literal;
        if (ctx != null && key != null && !key.isBlank()) {
            Double v = Resolvers.resolveDouble(ctx.getValue(key));
            if (v != null) return v;
        }
        return fallback;
    }

    private static boolean isValidTarget(Entity target, LivingEntity shooter, TeamManager teamManager, List<String> allowedTypes, List<UUID> allowedUuids) {
        if (target == null || target.equals(shooter)) return false;
        if (!(target instanceof LivingEntity)) return false;
        if (target.isDead()) return false;

        // Check specific UUIDs first
        if (allowedUuids != null && allowedUuids.contains(target.getUniqueId())) {
            return true;
        }

        // Check types
        if (allowedTypes != null && !allowedTypes.isEmpty()) {
            boolean typeMatch = false;
            for (String type : allowedTypes) {
                if (matchesType(target, type)) {
                    typeMatch = true;
                    break;
                }
            }
            if (!typeMatch) return false;
        } else {
            // Default to PLAYERS if no types specified AND no specific UUIDs
            if ((allowedUuids == null || allowedUuids.isEmpty()) && !(target instanceof Player)) return false;
        }

        if (target instanceof Player targetPlayer && shooter instanceof Player shooterPlayer && teamManager != null) {
            Team shooterTeam = teamManager.getTeam(shooterPlayer.getUniqueId());
            if (shooterTeam != null) {
                if (shooterTeam.getMembers().contains(targetPlayer.getUniqueId())) return false;
                Team targetTeam = teamManager.getTeam(targetPlayer.getUniqueId());
                if (targetTeam != null && shooterTeam.isAlliedWith(targetTeam.getName())) return false;
            }
        }
        
        if (target instanceof org.bukkit.entity.Tameable tameable && tameable.isTamed()) {
            var owner = tameable.getOwner();
            if (owner != null) {
                if (owner.getUniqueId().equals(shooter.getUniqueId())) return false;
                
                if (owner instanceof Player ownerPlayer && shooter instanceof Player shooterPlayer && teamManager != null) {
                    Team shooterTeam = teamManager.getTeam(shooterPlayer.getUniqueId());
                    if (shooterTeam != null) {
                        if (shooterTeam.getMembers().contains(ownerPlayer.getUniqueId())) return false;
                        Team ownerTeam = teamManager.getTeam(ownerPlayer.getUniqueId());
                        if (ownerTeam != null && shooterTeam.isAlliedWith(ownerTeam.getName())) return false;
                    }
                }
            }
        }

        return true;
    }

    private static boolean matchesType(Entity entity, String type) {
        if (type == null) return false;
        String t = type.trim().toUpperCase(Locale.ROOT);
        return switch (t) {
            case "PLAYER", "PLAYERS" -> entity instanceof Player;
            case "MOB", "MOBS" -> entity instanceof Mob;
            case "MONSTER", "MONSTERS", "HOSTILE", "HOSTILE_MOBS" -> entity instanceof Monster;
            case "ANIMAL", "ANIMALS", "PACIFIC", "PACIFIC_MOBS", "PASSIVE" -> 
                entity instanceof org.bukkit.entity.Animals || 
                entity instanceof org.bukkit.entity.WaterMob || 
                entity instanceof org.bukkit.entity.Ambient ||
                entity instanceof org.bukkit.entity.Villager ||
                entity instanceof org.bukkit.entity.WanderingTrader;
            case "PET", "PETS" -> entity instanceof org.bukkit.entity.Tameable tameable && tameable.isTamed();
            default -> {
                try {
                    yield entity.getType() == EntityType.valueOf(t);
                } catch (Exception e) {
                    yield false;
                }
            }
        };
    }

    private static LivingEntity findNearestTarget(Location from, double maxDistance, LivingEntity shooter, TeamManager teamManager, List<String> allowedTypes, List<UUID> allowedUuids) {
        if (from == null || from.getWorld() == null) return null;
        
        // If no criteria, no auto-target
        if ((allowedTypes == null || allowedTypes.isEmpty()) && (allowedUuids == null || allowedUuids.isEmpty())) return null;
        
        boolean onlyPlayers = (allowedTypes != null && allowedTypes.size() == 1 && (allowedTypes.get(0).equalsIgnoreCase("PLAYER") || allowedTypes.get(0).equalsIgnoreCase("PLAYERS")));

        java.util.Collection<? extends Entity> candidates;
        if (onlyPlayers && (allowedUuids == null || allowedUuids.isEmpty())) {
            candidates = from.getWorld().getPlayers();
        } else {
            candidates = from.getWorld().getNearbyEntities(from, maxDistance, maxDistance, maxDistance);
        }

        LivingEntity best = null;
        double bestSq = maxDistance * maxDistance;

        for (Entity e : candidates) {
            if (!(e instanceof LivingEntity le)) continue;
            if (!isValidTarget(le, shooter, teamManager, allowedTypes, allowedUuids)) continue;

            double dSq;
            try {
                dSq = e.getLocation().distanceSquared(from);
            } catch (Exception ignored) {
                continue;
            }
            
            if (dSq < bestSq) {
                bestSq = dSq;
                best = le;
            }
        }
        return best;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Projectile> projectileClassFromName(String raw) {
        if (raw == null) return null;
        String k = raw.trim().toUpperCase(Locale.ROOT);
        return switch (k) {
            case "SNOWBALL" -> (Class<? extends Projectile>) org.bukkit.entity.Snowball.class;
            case "ARROW" -> (Class<? extends Projectile>) org.bukkit.entity.Arrow.class;
            case "SPECTRAL_ARROW" -> (Class<? extends Projectile>) org.bukkit.entity.SpectralArrow.class;
            case "TRIDENT" -> (Class<? extends Projectile>) org.bukkit.entity.Trident.class;
            case "SMALL_FIREBALL" -> (Class<? extends Projectile>) org.bukkit.entity.SmallFireball.class;
            case "FIREBALL" -> (Class<? extends Projectile>) org.bukkit.entity.Fireball.class;
            case "DRAGON_FIREBALL" -> (Class<? extends Projectile>) org.bukkit.entity.DragonFireball.class;
            case "WITHER_SKULL" -> (Class<? extends Projectile>) org.bukkit.entity.WitherSkull.class;
            case "ENDER_PEARL" -> (Class<? extends Projectile>) org.bukkit.entity.EnderPearl.class;
            default -> (Class<? extends Projectile>) org.bukkit.entity.Snowball.class;
        };
    }
}
