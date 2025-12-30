package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class SetCompassTargetSpawnAction {
    private SetCompassTargetSpawnAction() {
    }

    static void register() {
        ActionRegistrar.register("set_compass_target_spawn", SetCompassTargetSpawnAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        return BuiltInActions.setCompassTargetSpawn();
    }
}
