package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Location;
import org.bukkit.Particle;

final class PlayParticleAction {
    private PlayParticleAction() {}

    static void register() {
        ActionRegistrar.register("play_particle", PlayParticleAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String particleName = Resolvers.string(null, raw, "particle", "type");
        Integer count = Resolvers.integer(null, raw, "count", "amount");
        Double speed = Resolvers.doubleVal(null, raw, "speed", "extra");
        Double spread = Resolvers.doubleVal(null, raw, "spread", "offset");
        Double dx = Resolvers.doubleVal(null, raw, "dx", "offset_x");
        Double dy = Resolvers.doubleVal(null, raw, "dy", "offset_y");
        Double dz = Resolvers.doubleVal(null, raw, "dz", "offset_z");
        Object locationSpecRaw = raw.get("location");
        if (locationSpecRaw == null) locationSpecRaw = raw.get("center");
        final Object locationSpec = locationSpecRaw;

        return ctx -> {
            if (particleName == null) return ActionResult.ALLOW;
            
            Particle particle;
            try {
                particle = Particle.valueOf(particleName.toUpperCase());
            } catch (Exception e) {
                return ActionResult.ALLOW;
            }

            Location loc = Resolvers.location(ctx, locationSpec);
            if (loc == null && ctx.location() != null) loc = ctx.location();
            if (loc == null && ctx.player() != null) loc = ctx.player().getLocation();
            if (loc == null) return ActionResult.ALLOW;

            int c = count != null ? count : 1;
            double s = speed != null ? speed : 0.0;
            double ox = dx != null ? dx : (spread != null ? spread : 0.0);
            double oy = dy != null ? dy : (spread != null ? spread : 0.0);
            double oz = dz != null ? dz : (spread != null ? spread : 0.0);

            final Location finalLoc = loc;
            ActionUtils.runSync(ctx.plugin(), () -> {
                finalLoc.getWorld().spawnParticle(particle, finalLoc, c, ox, oy, oz, s);
            });

            return ActionResult.ALLOW;
        };
    }
}
