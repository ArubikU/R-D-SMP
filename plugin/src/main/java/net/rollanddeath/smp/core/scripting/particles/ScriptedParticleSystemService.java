package net.rollanddeath.smp.core.scripting.particles;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Runtime para efectos de partículas con formas y fórmulas matemáticas.
 *
 * Diseñado para:
 * - One-shot (spawn inmediato)
 * - Sistemas persistentes (tick/periodo) con lifetime
 * - Follow de entidad (por UUID)
 */
public final class ScriptedParticleSystemService {

    private final RollAndDeathSMP plugin;
    private final Map<String, ActiveSystem> active = new ConcurrentHashMap<>();

    private BukkitTask tickTask;
    private long globalTick;

    public ScriptedParticleSystemService(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (plugin == null) return;
        if (tickTask == null || tickTask.isCancelled()) {
            tickTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 1L, 1L);
        }
    }

    public void stop() {
        if (tickTask != null && !tickTask.isCancelled()) {
            tickTask.cancel();
        }
        active.clear();
    }

    public boolean stopSystem(String id) {
        if (id == null || id.isBlank()) return false;
        return active.remove(id) != null;
    }

    public String startSystem(StartRequest req) {
        if (req == null) return null;
        String id = (req.id != null && !req.id.isBlank()) ? req.id : UUID.randomUUID().toString();

        ActiveSystem sys = new ActiveSystem(req, 0);
        active.put(id, sys);
        return id;
    }

    public void spawnNow(SpawnRequest req) {
        if (req == null) return;
        if (plugin == null) return;

        Runnable r = () -> {
            Location c = req.center != null ? req.center.clone() : null;
            if (c == null || c.getWorld() == null) return;
            c.add(req.centerOffset);
            emit(req, c, 0, 0);
        };

        if (Bukkit.isPrimaryThread()) {
            r.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, r);
        }
    }

    private void tick() {
        globalTick++;
        if (active.isEmpty()) return;

        for (var it = active.entrySet().iterator(); it.hasNext(); ) {
            var e = it.next();
            String id = e.getKey();
            ActiveSystem sys = e.getValue();
            if (id == null || sys == null) {
                it.remove();
                continue;
            }

            sys.ageTicks++;
            if (sys.req.lifetimeTicks > 0 && sys.ageTicks > sys.req.lifetimeTicks) {
                it.remove();
                continue;
            }

            int period = Math.max(1, sys.req.periodTicks);
            if ((sys.ageTicks % period) != 0) continue;

            Location center = resolveCenter(sys.req);
            if (center == null || center.getWorld() == null) continue;
            center.add(sys.req.centerOffset);

            emit(sys.req, center, (int) globalTick, sys.ageTicks);
        }
    }

    private Location resolveCenter(BaseRequest req) {
        if (req == null) return null;
        if (req.followEntityId != null) {
            Entity e = Bukkit.getEntity(req.followEntityId);
            if (e != null && e.isValid() && !e.isDead()) {
                try {
                    return e.getLocation().clone();
                } catch (Exception ignored) {
                }
            }
        }
        if (req.center != null) {
            try {
                return req.center.clone();
            } catch (Exception ignored) {
                return null;
            }
        }
        return null;
    }

    private void emit(BaseRequest req, Location center, int tick, int age) {
        if (req == null || center == null) return;
        World w = center.getWorld();
        if (w == null) return;

        Particle particle = req.spec.particle;
        if (particle == null) return;

        Shape shape = req.shape != null ? req.shape : Shape.POINT;
        int points = Math.max(1, req.points);

        switch (shape) {
            case POINT -> spawnAt(w, center, req.spec);
            case CIRCLE, RING -> {
                double radius = req.radius;
                double y = req.yOffset;
                for (int i = 0; i < points; i++) {
                    double t = i / (double) points;
                    double ang = (t * 2.0 * Math.PI) + req.angleOffset;
                    double x = Math.cos(ang) * radius;
                    double z = Math.sin(ang) * radius;
                    Location at = center.clone().add(x, y, z);
                    spawnAt(w, at, req.spec);
                }
            }
            case HELIX, SPIRAL -> {
                double radius = req.radius;
                double height = req.height;
                double turns = req.turns;
                for (int i = 0; i < points; i++) {
                    double t = points == 1 ? 0.0 : (i / (double) (points - 1));
                    double ang = (t * 2.0 * Math.PI * turns) + req.angleOffset;
                    double x = Math.cos(ang) * radius;
                    double z = Math.sin(ang) * radius;
                    double y = (t * height) + req.yOffset;
                    Location at = center.clone().add(x, y, z);
                    spawnAt(w, at, req.spec);
                }
            }
            case SPHERE -> {
                double r = req.radius;
                for (int i = 0; i < points; i++) {
                    // Distribución aproximada: Fibonacci sphere
                    double t = (i + 0.5) / (double) points;
                    double phi = Math.acos(1.0 - 2.0 * t);
                    double theta = (Math.PI * (1.0 + Math.sqrt(5.0))) * i + req.angleOffset;
                    double x = r * Math.sin(phi) * Math.cos(theta);
                    double y = r * Math.cos(phi) + req.yOffset;
                    double z = r * Math.sin(phi) * Math.sin(theta);
                    spawnAt(w, center.clone().add(x, y, z), req.spec);
                }
            }
            case CONE -> {
                double height = req.height;
                double radius = req.radius;
                for (int i = 0; i < points; i++) {
                    double t = points == 1 ? 0.0 : (i / (double) (points - 1));
                    double ang = (t * 2.0 * Math.PI * req.turns) + req.angleOffset;
                    double rr = radius * t;
                    double x = Math.cos(ang) * rr;
                    double z = Math.sin(ang) * rr;
                    double y = (t * height) + req.yOffset;
                    spawnAt(w, center.clone().add(x, y, z), req.spec);
                }
            }
            case FORMULA -> {
                if (req.formulaX == null || req.formulaY == null || req.formulaZ == null) return;

                Map<String, Double> vars = new HashMap<>();

                for (int i = 0; i < points; i++) {
                    double t = points == 1 ? 0.0 : (i / (double) (points - 1));
                    // Para FORMULA, angle va de 0 a 2π linealmente según t
                    // Permite que las fórmulas tengan control total del ángulo
                    double ang = (t * 2.0 * Math.PI) + req.angleOffset;
                    
                    // Recalcular variables en cada iteración para que rand() dé valores diferentes
                    vars.clear();
                    vars.put("tick", (double) tick);
                    vars.put("age", (double) age);
                    vars.put("time", tick / 20.0);
                    vars.put("radius", req.radius);
                    vars.put("r", req.radius);
                    vars.put("i", (double) i);
                    vars.put("t", t);
                    vars.put("angle", ang);

                    double x = req.formulaX.eval(vars);
                    double y = req.formulaY.eval(vars) + req.yOffset;
                    double z = req.formulaZ.eval(vars);

                    spawnAt(w, center.clone().add(x, y, z), req.spec);
                }
            }
        }
    }

    private static void spawnAt(World w, Location at, ParticleSpec spec) {
        if (w == null || at == null || spec == null) return;

        int count = Math.max(0, spec.count);
        double ox = Math.max(0.0, spec.offsetX);
        double oy = Math.max(0.0, spec.offsetY);
        double oz = Math.max(0.0, spec.offsetZ);

        try {
            if (spec.data != null) {
                w.spawnParticle(spec.particle, at, Math.max(1, count), ox, oy, oz, spec.extra, spec.data);
            } else {
                w.spawnParticle(spec.particle, at, Math.max(1, count), ox, oy, oz, spec.extra);
            }
        } catch (Exception ignored) {
        }
    }

    public enum Shape {
        POINT,
        CIRCLE,
        RING,
        HELIX,
        SPIRAL,
        SPHERE,
        CONE,
        FORMULA
    }

    public static final class ParticleSpec {
        public final Particle particle;
        public final int count;
        public final double offsetX;
        public final double offsetY;
        public final double offsetZ;
        public final double extra;
        public final Object data;

        public ParticleSpec(Particle particle, int count, double offsetX, double offsetY, double offsetZ, double extra, Object data) {
            this.particle = particle;
            this.count = count;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.extra = extra;
            this.data = data;
        }

        public static ParticleSpec from(
            String particleName,
            Integer count,
            Double offsetX,
            Double offsetY,
            Double offsetZ,
            Double extra,
            String dustColor,
            Double dustSize
        ) {
            if (particleName == null || particleName.isBlank()) return null;

            Particle p;
            try {
                p = Particle.valueOf(particleName.trim().toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
                String normalized = particleName.trim().toUpperCase(Locale.ROOT);
                if ("REDSTONE".equals(normalized)) {
                    p = Particle.DUST;
                } else {
                    return null;
                }
            }

            Object data = null;
            if (p == Particle.DUST) {
                Color c = parseColor(dustColor);
                float size = (float) Math.max(0.1, Math.min(10.0, dustSize != null ? dustSize : 1.0));
                data = new Particle.DustOptions(c != null ? c : Color.fromRGB(255, 0, 0), size);
            }

            return new ParticleSpec(
                p,
                count != null ? Math.max(0, count) : 1,
                offsetX != null ? offsetX : 0.0,
                offsetY != null ? offsetY : 0.0,
                offsetZ != null ? offsetZ : 0.0,
                extra != null ? extra : 0.0,
                data
            );
        }

        private static Color parseColor(String hex) {
            if (hex == null || hex.isBlank()) return null;
            String s = hex.trim();
            if (s.startsWith("#")) s = s.substring(1);
            if (s.length() != 6) return null;
            try {
                int rgb = Integer.parseInt(s, 16);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                return Color.fromRGB(r, g, b);
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    public abstract static class BaseRequest {
        public final Location center;
        public final UUID followEntityId;
        public final Vector centerOffset;

        public final ParticleSpec spec;

        public final Shape shape;
        public final int points;
        public final double radius;
        public final double height;
        public final double turns;
        public final double yOffset;
        public final double angleOffset;

        public final MathExpression.Compiled formulaX;
        public final MathExpression.Compiled formulaY;
        public final MathExpression.Compiled formulaZ;

        protected BaseRequest(
            Location center,
            UUID followEntityId,
            Vector centerOffset,
            ParticleSpec spec,
            Shape shape,
            int points,
            double radius,
            double height,
            double turns,
            double yOffset,
            double angleOffset,
            MathExpression.Compiled formulaX,
            MathExpression.Compiled formulaY,
            MathExpression.Compiled formulaZ
        ) {
            this.center = center;
            this.followEntityId = followEntityId;
            this.centerOffset = centerOffset != null ? centerOffset : new Vector(0, 0, 0);
            this.spec = spec;
            this.shape = shape;
            this.points = points;
            this.radius = radius;
            this.height = height;
            this.turns = turns;
            this.yOffset = yOffset;
            this.angleOffset = angleOffset;
            this.formulaX = formulaX;
            this.formulaY = formulaY;
            this.formulaZ = formulaZ;
        }
    }

    public static final class SpawnRequest extends BaseRequest {
        public SpawnRequest(
            Location center,
            UUID followEntityId,
            Vector centerOffset,
            ParticleSpec spec,
            Shape shape,
            int points,
            double radius,
            double height,
            double turns,
            double yOffset,
            double angleOffset,
            MathExpression.Compiled formulaX,
            MathExpression.Compiled formulaY,
            MathExpression.Compiled formulaZ
        ) {
            super(center, followEntityId, centerOffset, spec, shape, points, radius, height, turns, yOffset, angleOffset, formulaX, formulaY, formulaZ);
        }
    }

    public static final class StartRequest extends BaseRequest {
        public final String id;
        public final int lifetimeTicks;
        public final int periodTicks;

        public StartRequest(
            String id,
            Location center,
            UUID followEntityId,
            Vector centerOffset,
            ParticleSpec spec,
            Shape shape,
            int points,
            double radius,
            double height,
            double turns,
            double yOffset,
            double angleOffset,
            MathExpression.Compiled formulaX,
            MathExpression.Compiled formulaY,
            MathExpression.Compiled formulaZ,
            int lifetimeTicks,
            int periodTicks
        ) {
            super(center, followEntityId, centerOffset, spec, shape, points, radius, height, turns, yOffset, angleOffset, formulaX, formulaY, formulaZ);
            this.id = id;
            this.lifetimeTicks = lifetimeTicks;
            this.periodTicks = periodTicks;
        }
    }

    private static final class ActiveSystem {
        private final StartRequest req;
        private int ageTicks;

        private ActiveSystem(StartRequest req, int ageTicks) {
            this.req = req;
            this.ageTicks = ageTicks;
        }
    }
}
