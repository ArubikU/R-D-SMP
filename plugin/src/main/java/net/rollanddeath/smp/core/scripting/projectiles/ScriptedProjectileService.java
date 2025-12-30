package net.rollanddeath.smp.core.scripting.projectiles;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import net.rollanddeath.smp.core.scripting.ScriptPhase;
import net.rollanddeath.smp.core.scripting.ScriptVars;
import net.rollanddeath.smp.core.scripting.particles.MathExpression;
import net.rollanddeath.smp.core.scripting.particles.ScriptedParticleSystemService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio para proyectiles controlados por scripting (trayectorias curvadas / homing) con partículas y explosión.
 */
public final class ScriptedProjectileService implements Listener {

    private final RollAndDeathSMP plugin;

    private final Map<UUID, ActiveProjectile> active = new ConcurrentHashMap<>();

    private BukkitTask tickTask;

    public ScriptedProjectileService(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    public void start() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        if (tickTask == null || tickTask.isCancelled()) {
            tickTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
        }
    }

    public void stop() {
        HandlerList.unregisterAll(this);
        if (tickTask != null && !tickTask.isCancelled()) {
            tickTask.cancel();
        }
        active.clear();
    }

    public boolean isManaged(Projectile projectile) {
        return projectile != null && active.containsKey(projectile.getUniqueId());
    }

    public void launchCurvedProjectile(LaunchRequest req) {
        if (req == null) return;
        if (plugin == null) return;

        Runnable task = () -> {
            Location start = safeClone(req.start);
            if (start == null || start.getWorld() == null) return;

            Projectile projectile;
            try {
                projectile = start.getWorld().spawn(start, req.projectileClass, p -> {
                    p.setShooter(req.shooter);
                    try {
                        p.setGravity(false);
                    } catch (Exception ignored) {
                    }
                });
            } catch (Exception ignored) {
                return;
            }

            ActiveProjectile ap = new ActiveProjectile(
                projectile,
                req,
                0
            );
            active.put(projectile.getUniqueId(), ap);

            // onLaunch hook
            if (req.onLaunch != null && !req.onLaunch.isEmpty()) {
                runHook(ap, "scripted_projectile_launch", start, null, null, null, req.onLaunch, req.shooter);
            }
        };

        Bukkit.getScheduler().runTask(plugin, task);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile p = event.getEntity();
        ActiveProjectile ap = active.get(p.getUniqueId());
        if (ap == null) return;

        // Target filtering
        if (event.getHitEntity() != null) {
            boolean hasTargets = (ap.req.targets != null && !ap.req.targets.isEmpty());
            boolean hasUuids = (ap.req.allowedUuids != null && !ap.req.allowedUuids.isEmpty());
            
            if (hasTargets || hasUuids) {
                boolean valid = false;
                
                // Check UUIDs first
                if (hasUuids && ap.req.allowedUuids.contains(event.getHitEntity().getUniqueId())) {
                    valid = true;
                }
                
                // Check types if not already valid
                if (!valid && hasTargets) {
                    for (String t : ap.req.targets) {
                        if (t.equalsIgnoreCase("PLAYER") || t.equalsIgnoreCase("PLAYERS")) {
                            if (event.getHitEntity() instanceof Player) {
                                valid = true;
                                break;
                            }
                        } else if (t.equalsIgnoreCase("MOB") || t.equalsIgnoreCase("MOBS")) {
                            if (event.getHitEntity() instanceof LivingEntity && !(event.getHitEntity() instanceof Player)) {
                                valid = true;
                                break;
                            }
                        } else if (t.equalsIgnoreCase("ALL") || t.equalsIgnoreCase("ANY")) {
                            valid = true;
                            break;
                        }
                    }
                }
                
                if (!valid) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        // Impacto: detonamos si aplica.
        if (ap.req.explodeOnImpact) {
            Location impact = p.getLocation();
            try {
                if (event.getHitBlock() != null) {
                    impact = event.getHitBlock().getLocation().add(0.5, 0.5, 0.5);
                } else if (event.getHitEntity() != null) {
                    impact = event.getHitEntity().getLocation();
                }
            } catch (Exception ignored) {
            }
            detonate(ap, impact, event.getHitEntity(), event.getHitBlock(), event);
        } else {
            // Aun si no explota, podemos ejecutar onHit
            runHook(ap, "scripted_projectile_hit", p.getLocation(), event.getHitEntity(), event.getHitBlock(), event, ap.req.onHit, ap.req.shooter);
        }

        // Siempre removemos: estos proyectiles son controlados por el engine.
        cleanup(p.getUniqueId());
        try {
            p.remove();
        } catch (Exception ignored) {
        }
    }

    private void tick() {
        if (active.isEmpty()) return;

        for (ActiveProjectile ap : active.values()) {
            Projectile p = ap.projectile;
            if (p == null) continue;
            if (!p.isValid() || p.isDead()) {
                cleanup(p.getUniqueId());
                continue;
            }

            LaunchRequest req = ap.req;
            if (req == null) {
                cleanup(p.getUniqueId());
                continue;
            }

            int age = ap.ageTicks + 1;
            ap.ageTicks = age;

            Location current = p.getLocation();
            if (current == null || current.getWorld() == null) {
                cleanup(p.getUniqueId());
                continue;
            }

            // Resuelve destino (homing opcional)
            Location end = safeClone(req.end);
            if (req.homing && req.targetEntityId != null) {
                Entity t = Bukkit.getEntity(req.targetEntityId);
                if (t != null && t.isValid() && !t.isDead() && t.getLocation() != null) {
                    end = t.getLocation().clone().add(0, req.targetYOffset, 0);
                }
            }

            if (end == null || end.getWorld() == null || end.getWorld() != current.getWorld()) {
                end = current; // fail-safe
            }

            int total = Math.max(1, req.durationTicks);
            double t = Math.min(1.0, age / (double) total);

            // Cálculo de Bezier
            Location p0 = safeClone(req.start);
            if (p0 == null || p0.getWorld() == null) p0 = current;

            Vector c1 = req.control1 != null ? req.control1.clone() : computeControlPoint(p0, end, 0.33, req.curveHeight, req.curveSide);
            Vector c2 = req.control2 != null ? req.control2.clone() : computeControlPoint(p0, end, 0.66, req.curveHeight, req.curveSide);

            Vector nextPos = cubicBezier(
                p0.toVector(),
                c1,
                c2,
                end.toVector(),
                t
            );

            Location next = new Location(current.getWorld(), nextPos.getX(), nextPos.getY(), nextPos.getZ(), current.getYaw(), current.getPitch());

            // onTick actions
            if (req.onTick != null && !req.onTick.isEmpty()) {
                runHook(ap, "scripted_projectile_tick", current, null, null, null, req.onTick, ap.projectile);
            }

            // Fin (llegó)
            if (t >= 1.0 || age >= total) {
                if (req.explodeOnFinish) {
                    detonate(ap, end, null, null, null);
                } else {
                    runHook(ap, "scripted_projectile_finish", end, null, null, null, req.onFinish, ap.projectile);
                }
                cleanup(p.getUniqueId());
                try {
                    p.remove();
                } catch (Exception ignored) {
                }
                continue;
            }

            // Movimiento controlado
            try {
                // Orientación: miramos a donde vamos
                Vector vel = next.toVector().subtract(current.toVector());
                p.setVelocity(vel);

                // Importante: NO usar teleport() cada tick.
                // El cliente no interpola teleports y se ve como "tepeo".
                // Con velocity el cliente interpola movimiento suavemente.
                if (vel.lengthSquared() > 1.0e-6) {
                    Vector dir = vel.clone().normalize();
                    float yaw = (float) Math.toDegrees(Math.atan2(-dir.getX(), dir.getZ()));
                    float pitch = (float) Math.toDegrees(Math.asin(-dir.getY()));
                    p.setRotation(yaw, pitch);
                }
            } catch (Exception ignored) {
            }
        }
    }



    private void detonate(ActiveProjectile ap, Location at, Entity hitEntity, org.bukkit.block.Block hitBlock, Object eventOrNull) {
        LaunchRequest req = ap.req;
        if (req == null) return;

        Location loc = safeClone(at);
        if (loc == null || loc.getWorld() == null) return;

        // Ejecuta hook primero (permite, por ejemplo, spawn de partículas extra)
        runHook(ap, "scripted_projectile_detonate", loc, hitEntity, hitBlock, eventOrNull, req.onHit, req.shooter);

        // Explosión
        float power = (float) Math.max(0.0, req.explosionPower);
        boolean fire = req.explosionFire;
        boolean breakBlocks = req.explosionBreakBlocks;

        try {
            loc.getWorld().createExplosion(loc, power, fire, breakBlocks, req.shooter);
        } catch (Exception ignored) {
        }

        // Hook de finish (post)
        runHook(ap, "scripted_projectile_finish", loc, hitEntity, hitBlock, eventOrNull, req.onFinish, ap.projectile);
    }

    private void runHook(ActiveProjectile ap, String eventName, Location impact, Entity hitEntity, org.bukkit.block.Block hitBlock, Object eventOrNull, List<Action> actions, Entity subjectOverride) {
        if (actions == null || actions.isEmpty()) return;

        ScriptInvocation inv = ap.req.invocation;
        if (inv == null) return;

        Player player = inv.player;
        if (player == null && ap.req.targetEntityId != null) {
            Entity e = Bukkit.getEntity(ap.req.targetEntityId);
            if (e instanceof Player p) player = p;
        }

        Entity resolvedTarget = hitEntity;
        if (resolvedTarget == null && ap.req.targetEntityId != null) {
            try {
                Entity e = Bukkit.getEntity(ap.req.targetEntityId);
                if (e != null && e.isValid() && !e.isDead()) resolvedTarget = e;
            } catch (Exception ignored) {
            }
        }

        Map<String, Object> vars = ScriptVars.create()
            .merge(inv.baseVars)
            // 100% scopes: SUBJECT/TARGET/PROJECTILE y EVENT (reflectivo).
            .subject(subjectOverride != null ? subjectOverride : ap.req.shooter)
            .target(resolvedTarget)
            .projectile(ap.projectile)
            .event(eventOrNull)
            .build();

        ScriptContext ctx = new ScriptContext(plugin, player, inv.subjectId, inv.phase, vars);
        ScriptEngine.runAllWithResult(ctx, actions);
    }

    private void cleanup(UUID id) {
        if (id == null) return;
        active.remove(id);
    }

    private static Location safeClone(Location loc) {
        try {
            return loc != null ? loc.clone() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Vector computeControlPoint(Location start, Location end, double along, double height, double side) {
        Vector s = start.toVector();
        Vector e = end.toVector();
        Vector dir = e.clone().subtract(s);
        double dist = dir.length();
        if (dist <= 0.0001) dist = 1.0;
        dir.normalize();

        Vector up = new Vector(0, 1, 0);
        Vector right = dir.clone().crossProduct(up);
        if (right.lengthSquared() < 0.0001) {
            // dir paralelo a up; usamos eje X
            right = new Vector(1, 0, 0);
        } else {
            right.normalize();
        }

        return s.clone()
            .add(dir.clone().multiply(dist * along))
            .add(up.clone().multiply(height))
            .add(right.clone().multiply(side));
    }

    private static Vector cubicBezier(Vector p0, Vector c1, Vector c2, Vector p3, double t) {
        double u = 1.0 - t;
        double tt = t * t;
        double uu = u * u;
        double uuu = uu * u;
        double ttt = tt * t;

        Vector out = p0.clone().multiply(uuu);
        out.add(c1.clone().multiply(3 * uu * t));
        out.add(c2.clone().multiply(3 * u * tt));
        out.add(p3.clone().multiply(ttt));
        return out;
    }

    private static final class ActiveProjectile {
        private final Projectile projectile;
        private final LaunchRequest req;
        private int ageTicks;

        private ActiveProjectile(Projectile projectile, LaunchRequest req, int ageTicks) {
            this.projectile = projectile;
            this.req = req;
            this.ageTicks = ageTicks;
        }
    }

    public static final class ScriptInvocation {
        public final String subjectId;
        public final ScriptPhase phase;
        public final Player player;
        public final Map<String, Object> baseVars;

        public ScriptInvocation(String subjectId, ScriptPhase phase, Player player, Map<String, Object> baseVars) {
            this.subjectId = subjectId;
            this.phase = phase;
            this.player = player;
            this.baseVars = baseVars;
        }
    }

    public static final class LaunchRequest {
        public final Class<? extends Projectile> projectileClass;
        public final LivingEntity shooter;
        public final Location start;
        public final Location end;
        public final UUID targetEntityId;
        public final double targetYOffset;
        public final boolean homing;

        public final int durationTicks;
        public final double curveHeight;
        public final double curveSide;
        public final Vector control1;
        public final Vector control2;

        public final boolean explodeOnImpact;
        public final boolean explodeOnFinish;
        public final double explosionPower;
        public final boolean explosionFire;
        public final boolean explosionBreakBlocks;

        public final List<Action> onHit;
        public final List<Action> onFinish;
        public final List<Action> onTick;
        public final List<Action> onLaunch;
        public final List<String> targets;
        public final List<UUID> allowedUuids;

        public final ScriptInvocation invocation;

        public LaunchRequest(
            Class<? extends Projectile> projectileClass,
            LivingEntity shooter,
            Location start,
            Location end,
            UUID targetEntityId,
            double targetYOffset,
            boolean homing,
            int durationTicks,
            double curveHeight,
            double curveSide,
            Vector control1,
            Vector control2,
            boolean explodeOnImpact,
            boolean explodeOnFinish,
            double explosionPower,
            boolean explosionFire,
            boolean explosionBreakBlocks,
            List<Action> onHit,
            List<Action> onFinish,
            List<Action> onTick,
            List<Action> onLaunch,
            List<String> targets,
            List<UUID> allowedUuids,
            ScriptInvocation invocation
        ) {
            this.projectileClass = projectileClass;
            this.shooter = shooter;
            this.start = start;
            this.end = end;
            this.targetEntityId = targetEntityId;
            this.targetYOffset = targetYOffset;
            this.homing = homing;
            this.durationTicks = durationTicks;
            this.curveHeight = curveHeight;
            this.curveSide = curveSide;
            this.control1 = control1;
            this.control2 = control2;
            this.explodeOnImpact = explodeOnImpact;
            this.explodeOnFinish = explodeOnFinish;
            this.explosionPower = explosionPower;
            this.explosionFire = explosionFire;
            this.explosionBreakBlocks = explosionBreakBlocks;
            this.onHit = onHit;
            this.onFinish = onFinish;
            this.onTick = onTick;
            this.onLaunch = onLaunch;
            this.targets = targets;
            this.allowedUuids = allowedUuids;
            this.invocation = invocation;
        }
    }
}
