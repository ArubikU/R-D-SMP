package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class AddVelocityRandomAction {
    private AddVelocityRandomAction() {
    }

    static void register() {
        ActionRegistrar.register("add_velocity_random", AddVelocityRandomAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Double minX = Resolvers.doubleVal(null, raw, "min_x");
        Double maxX = Resolvers.doubleVal(null, raw, "max_x");
        Double minY = Resolvers.doubleVal(null, raw, "min_y");
        Double maxY = Resolvers.doubleVal(null, raw, "max_y");
        Double minZ = Resolvers.doubleVal(null, raw, "min_z");
        Double maxZ = Resolvers.doubleVal(null, raw, "max_z");
        if (minX == null && maxX == null && minY == null && maxY == null && minZ == null && maxZ == null) return null;
        return BuiltInActions.addVelocityRandom(
            minX != null ? minX : 0.0,
            maxX != null ? maxX : (minX != null ? minX : 0.0),
            minY != null ? minY : 0.0,
            maxY != null ? maxY : (minY != null ? minY : 0.0),
            minZ != null ? minZ : 0.0,
            maxZ != null ? maxZ : (minZ != null ? minZ : 0.0)
        );
    }
}
