package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class SetFireTicksAction {
    private SetFireTicksAction() {
    }

    static void register() {
        ActionRegistrar.register("set_fire_ticks", SetFireTicksAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Integer ticks = Resolvers.integer(null, raw, "ticks");
        if (ticks == null) return null;
        return BuiltInActions.setFireTicks(ticks);
    }
}
