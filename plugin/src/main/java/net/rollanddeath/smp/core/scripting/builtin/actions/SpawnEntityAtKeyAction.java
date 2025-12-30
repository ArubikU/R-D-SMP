package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class SpawnEntityAtKeyAction {
    private SpawnEntityAtKeyAction() {
    }

    static void register() {
        ActionRegistrar.register("spawn_entity_at_key", SpawnEntityAtKeyAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object where = firstNonNull(raw, "where", "location", "key", "location_key");
        String entityType = Resolvers.string(null, raw, "entity_type");
        if (where == null || entityType == null || entityType.isBlank()) return null;
        Integer count = Resolvers.integer(null, raw, "count");
        Integer radius = Resolvers.integer(null, raw, "radius");
        Integer yOffset = Resolvers.integer(null, raw, "y_offset");
        String name = Resolvers.string(null, raw, "name");
        Boolean nameVisible = raw.get("name_visible") instanceof Boolean b ? b : null;
        Integer beeAngerTicks = Resolvers.integer(null, raw, "bee_anger_ticks");
        String storeKey = Resolvers.string(null, raw, "store_key", "store_as", "entity_store", "out");
        return BuiltInActions.spawnEntityAt(
            where,
            entityType,
            count != null ? Math.max(1, count) : 1,
            radius != null ? Math.max(0, radius) : 0,
            yOffset != null ? yOffset : 0,
            name,
            nameVisible != null ? nameVisible : true,
            beeAngerTicks != null ? Math.max(0, beeAngerTicks) : null,
            storeKey
        );
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
