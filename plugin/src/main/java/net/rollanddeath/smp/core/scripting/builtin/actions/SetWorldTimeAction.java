package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.World;

final class SetWorldTimeAction {
    private SetWorldTimeAction() {
    }

    static void register() {
        ActionRegistrar.register("set_world_time", SetWorldTimeAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Long time = Resolvers.longVal(null, raw, "time");
        if (time == null) {
            Integer t = Resolvers.integer(null, raw, "time");
            if (t != null) time = (long) t;
        }
        if (time == null) return null;
        
        long finalTime = time;
        return ctx -> {
            World w = ctx.world();
            if (w != null) {
                w.setTime(finalTime);
            }
            return ActionResult.ALLOW;
        };
    }
}
