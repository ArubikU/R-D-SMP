package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

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

        return BuiltInActions.gravityPullNearLocation(center, radius, strength, strengthKey, includePlayers, includeMobs, excludeCaster, excludeSpectators, scaleByDistance, maxForce, atTargetActions);
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
