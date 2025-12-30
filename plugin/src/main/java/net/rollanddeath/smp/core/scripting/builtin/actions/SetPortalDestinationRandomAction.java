package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class SetPortalDestinationRandomAction {
    private SetPortalDestinationRandomAction() {
    }

    static void register() {
        ActionRegistrar.register("set_portal_destination_random", SetPortalDestinationRandomAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Integer minX = Resolvers.integer(null, raw, "min_x");
        Integer maxX = Resolvers.integer(null, raw, "max_x");
        Integer minZ = Resolvers.integer(null, raw, "min_z");
        Integer maxZ = Resolvers.integer(null, raw, "max_z");
        Integer y = Resolvers.integer(null, raw, "y");
        boolean useHighest = raw.get("use_highest_block") instanceof Boolean b ? b : true;
        if (minX == null || maxX == null || minZ == null || maxZ == null) return null;
        return BuiltInActions.setPortalDestinationRandom(minX, maxX, minZ, maxZ, y, useHighest);
    }
}
