package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

final class GravityPullNearLocationAction {
    private GravityPullNearLocationAction() {
    }

    static void register() {
        ActionRegistrar.register("gravity_pull_near_location", GravityPullNearLocationAction::parse, "gravity_pull");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object center = firstNonNull(raw, "center", "location", "at", "center_key");
        Double radius = Resolvers.doubleVal(null, raw, "radius", "r");
        Double strength = Resolvers.doubleVal(null, raw, "strength", "speed");
        String strengthKey = Resolvers.string(null, raw, "strength_key", "speed_key");

        if (center == null) return null;
        if (radius == null || radius <= 0.0) return null;
        if ((strength == null || strength == 0.0) && (strengthKey == null || strengthKey.isBlank())) return null;

        boolean includePlayers = raw.get("include_players") instanceof Boolean b ? b : true;
        boolean includeMobs = raw.get("include_mobs") instanceof Boolean b ? b : true;
        boolean excludeCaster = raw.get("exclude_subject") instanceof Boolean b ? b
            : (raw.get("exclude_caster") instanceof Boolean b2 ? b2 : true);
        boolean excludeSpectators = raw.get("exclude_spectators") instanceof Boolean b ? b : true;
        boolean scaleByDistance = raw.get("scale_by_distance") instanceof Boolean b ? b : true;
        Double maxForce = Resolvers.doubleVal(null, raw, "max_force", "max_strength");

        List<Action> atTargetActions = Resolvers.parseActionList(raw.get("at_target"));

        return ctx -> {
            Location loc = Resolvers.location(ctx, center);
            if (loc == null) return ActionResult.ALLOW;

            double r = radius;
            double str = strength != null ? strength : 0.0;
            if (strengthKey != null) {
                Double d = Resolvers.doubleVal(ctx, strengthKey);
                if (d != null) str = d;
            }
            if (str == 0.0) return ActionResult.ALLOW;

            double finalStr = str;

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity e : loc.getWorld().getNearbyEntities(loc, r, r, r)) {
                    if (e.getLocation().distanceSquared(loc) > r * r) continue;

                    if (!includePlayers && e instanceof Player) continue;
                    if (!includeMobs && !(e instanceof Player)) continue;
                    if (excludeSpectators && e instanceof Player p && p.getGameMode() == org.bukkit.GameMode.SPECTATOR) continue;

                    if (excludeCaster) {
                        Entity subject = ctx.subject();
                        if (subject != null && subject.getUniqueId().equals(e.getUniqueId())) continue;
                    }

                    Vector dir = loc.toVector().subtract(e.getLocation().toVector());
                    double dist = dir.length();
                    dir.normalize();

                    double force = finalStr;
                    if (scaleByDistance && dist > 0) {
                        force = finalStr * (1.0 - (dist / r));
                    }

                    if (maxForce != null && Math.abs(force) > maxForce) {
                        force = Math.signum(force) * maxForce;
                    }

                    e.setVelocity(e.getVelocity().add(dir.multiply(force)));

                    if (atTargetActions != null && !atTargetActions.isEmpty()) {
                        java.util.Map<String, Object> vars = new java.util.HashMap<>(ctx.variables());
                        vars.put("__subject", e);
                        vars.put("__target", e);

                        Player p = (e instanceof Player) ? (Player) e : ctx.player();
                        ScriptContext subCtx = new ScriptContext(ctx.plugin(), p, ctx.subjectId(), ctx.phase(), vars);
                        ScriptEngine.runAll(subCtx, atTargetActions);
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }

    private static Object firstNonNull(java.util.Map<?, ?> raw, String... keys) {
        if (raw == null || keys == null) return null;
        for (String k : keys) {
            if (k == null || k.isBlank()) continue;
            Object v = raw.get(k);
            if (v != null) return v;
        }
        return null;
    }
}
