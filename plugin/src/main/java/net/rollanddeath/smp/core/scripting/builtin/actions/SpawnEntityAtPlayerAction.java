package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class SpawnEntityAtPlayerAction {
    private SpawnEntityAtPlayerAction() {
    }

    static void register() {
        ActionRegistrar.register("spawn_entity_at_player", SpawnEntityAtPlayerAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String entityType = Resolvers.string(null, raw, "entity_type");
        if (entityType == null || entityType.isBlank()) return null;
        Integer dx = Resolvers.integer(null, raw, "offset_x");
        Integer dy = Resolvers.integer(null, raw, "offset_y");
        Integer dz = Resolvers.integer(null, raw, "offset_z");
        return BuiltInActions.spawnEntityAtPlayer(entityType,
            dx != null ? dx : 0,
            dy != null ? dy : 0,
            dz != null ? dz : 0);
    }
}
