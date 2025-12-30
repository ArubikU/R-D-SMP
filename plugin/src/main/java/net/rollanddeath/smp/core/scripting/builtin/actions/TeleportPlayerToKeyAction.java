package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class TeleportPlayerToKeyAction {
    private TeleportPlayerToKeyAction() {
    }

    static void register() {
        ActionRegistrar.register("teleport_player_to_key", TeleportPlayerToKeyAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object where = firstNonNull(raw, "where", "location", "key", "location_key");
        if (where == null) return null;
        return BuiltInActions.teleportPlayerTo(where);
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
