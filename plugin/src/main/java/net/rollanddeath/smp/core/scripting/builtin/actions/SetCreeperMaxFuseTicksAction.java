package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class SetCreeperMaxFuseTicksAction {
    private SetCreeperMaxFuseTicksAction() {
    }

    static void register() {
        ActionRegistrar.register("set_creeper_max_fuse_ticks", SetCreeperMaxFuseTicksAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Integer ticks = Resolvers.integer(null, raw, "ticks");
        if (ticks == null || ticks < 1) return null;
        return BuiltInActions.setCreeperMaxFuseTicks(ticks);
    }
}
