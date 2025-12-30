package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class SetCompassTargetRandomAction {
    private SetCompassTargetRandomAction() {
    }

    static void register() {
        ActionRegistrar.register("set_compass_target_random", SetCompassTargetRandomAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Integer minX = Resolvers.integer(null, raw, "min_x");
        Integer maxX = Resolvers.integer(null, raw, "max_x");
        Integer minZ = Resolvers.integer(null, raw, "min_z");
        Integer maxZ = Resolvers.integer(null, raw, "max_z");
        Integer y = Resolvers.integer(null, raw, "y");
        if (minX == null || maxX == null || minZ == null || maxZ == null) return null;
        return BuiltInActions.setCompassTargetRandom(minX, maxX, minZ, maxZ, y != null ? y : 64);
    }
}
