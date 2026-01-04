package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.particles.MathExpression;
import net.rollanddeath.smp.core.scripting.particles.ScriptedParticleSystemService;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

final class SpawnParticleShapeAction {
    private SpawnParticleShapeAction() {}

    static void register() {
        ActionRegistrar.register("spawn_particle_shape", SpawnParticleShapeAction::parse, "spawn_particles");
    }

    private static Action parse(Map<?, ?> raw) {
        Object centerSpecRaw = raw.get("center");
        if (centerSpecRaw == null) centerSpecRaw = raw.get("location");
        if (centerSpecRaw == null) centerSpecRaw = raw.get("center_key");
        final Object centerSpec = centerSpecRaw;
        
        Object followEntitySpecRaw = raw.get("follow_entity");
        if (followEntitySpecRaw == null) followEntitySpecRaw = raw.get("follow_entity_key");
        final Object followEntitySpec = followEntitySpecRaw;
        
        String particle = Resolvers.string(null, raw, "particle");
        Integer count = Resolvers.integer(null, raw, "count");
        Double offsetX = Resolvers.doubleVal(null, raw, "offset_x");
        Double offsetY = Resolvers.doubleVal(null, raw, "offset_y");
        Double offsetZ = Resolvers.doubleVal(null, raw, "offset_z");
        Double extra = Resolvers.doubleVal(null, raw, "extra", "speed");
        String dustColor = Resolvers.string(null, raw, "dust_color", "color");
        Double dustSize = Resolvers.doubleVal(null, raw, "dust_size", "size");
        
        String shapeRaw = Resolvers.string(null, raw, "shape");
        Integer points = Resolvers.integer(null, raw, "points");
        Double radius = Resolvers.doubleVal(null, raw, "radius");
        Double height = Resolvers.doubleVal(null, raw, "height");
        Double turns = Resolvers.doubleVal(null, raw, "turns");
        Double yOffset = Resolvers.doubleVal(null, raw, "y_offset");
        Double angleOffset = Resolvers.doubleVal(null, raw, "angle_offset");
        
        String formulaX = Resolvers.string(null, raw, "formula_x");
        String formulaY = Resolvers.string(null, raw, "formula_y");
        String formulaZ = Resolvers.string(null, raw, "formula_z");
        
        Double centerOffsetX = Resolvers.doubleVal(null, raw, "center_offset_x");
        Double centerOffsetY = Resolvers.doubleVal(null, raw, "center_offset_y");
        Double centerOffsetZ = Resolvers.doubleVal(null, raw, "center_offset_z");

        return ctx -> {
            var plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            ScriptedParticleSystemService svc = plugin.getScriptedParticleSystemService();
            if (svc == null) return ActionResult.ALLOW;

            Entity followEnt = Resolvers.entity(ctx, followEntitySpec);
            UUID followId = followEnt != null ? followEnt.getUniqueId() : null;

            Location center = null;
            if (followId == null) {
                World defaultWorld = ctx.player() != null ? ctx.player().getWorld() : null;
                Object cObj = centerSpec != null ? centerSpec : "caster";
                center = Resolvers.location(ctx, cObj, defaultWorld);
                if (center == null && ctx.player() != null) {
                    center = ctx.player().getLocation();
                }
                if (center == null) {
                    center = ctx.location();
                }
                if (center == null) {
                    org.bukkit.event.entity.EntityEvent ee = ctx.nativeEvent(org.bukkit.event.entity.EntityEvent.class);
                    if (ee != null) center = ee.getEntity().getLocation();
                }
            }

            ScriptedParticleSystemService.ParticleSpec spec = ScriptedParticleSystemService.ParticleSpec.from(
                particle,
                count,
                offsetX,
                offsetY,
                offsetZ,
                extra,
                dustColor,
                dustSize
            );
            if (spec == null) return ActionResult.ALLOW;

            ScriptedParticleSystemService.Shape shape = parseParticleShape(shapeRaw);
            int pts = points != null ? Math.max(1, points) : 16;
            double r = radius != null ? radius : 1.0;
            double h = height != null ? height : 2.0;
            double tr = turns != null ? turns : 1.0;
            double yo = yOffset != null ? yOffset : 0.0;
            double ao = angleOffset != null ? angleOffset : 0.0;

            MathExpression.Compiled fx = shape == ScriptedParticleSystemService.Shape.FORMULA ? MathExpression.compile(formulaX) : null;
            MathExpression.Compiled fy = shape == ScriptedParticleSystemService.Shape.FORMULA ? MathExpression.compile(formulaY) : null;
            MathExpression.Compiled fz = shape == ScriptedParticleSystemService.Shape.FORMULA ? MathExpression.compile(formulaZ) : null;

            Vector centerOffset = new Vector(
                centerOffsetX != null ? centerOffsetX : 0.0,
                centerOffsetY != null ? centerOffsetY : 0.0,
                centerOffsetZ != null ? centerOffsetZ : 0.0
            );

            svc.spawnNow(new ScriptedParticleSystemService.SpawnRequest(
                center,
                followId,
                centerOffset,
                spec,
                shape,
                pts,
                r,
                h,
                tr,
                yo,
                ao,
                fx,
                fy,
                fz
            ));

            return ActionResult.ALLOW;
        };
    }

    private static ScriptedParticleSystemService.Shape parseParticleShape(String raw) {
        if (raw == null || raw.isBlank()) return ScriptedParticleSystemService.Shape.POINT;
        String s = raw.trim().toUpperCase(Locale.ROOT);
        try {
            return ScriptedParticleSystemService.Shape.valueOf(s);
        } catch (Exception ignored) {
            return ScriptedParticleSystemService.Shape.POINT;
        }
    }
}
