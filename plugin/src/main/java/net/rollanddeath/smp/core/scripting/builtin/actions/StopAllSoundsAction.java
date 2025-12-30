package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class StopAllSoundsAction {
    private StopAllSoundsAction() {
    }

    static void register() {
        ActionRegistrar.register("stop_all_sounds", StopAllSoundsAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        return BuiltInActions.stopAllSounds();
    }
}
