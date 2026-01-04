package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.FishHook;
import org.bukkit.event.player.PlayerFishEvent;

final class LegendaryFisherAction {
    private LegendaryFisherAction() {}

    static void register() {
        ActionRegistrar.register("legendary_fisher", LegendaryFisherAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Integer min = Resolvers.integer(null, raw, "min_wait_ticks");
        Integer max = Resolvers.integer(null, raw, "max_wait_ticks");
        Boolean applyLure = Resolvers.bool(null, raw, "apply_lure");
        
        int minTicks = min != null ? min : 100;
        int maxTicks = max != null ? max : 600;
        boolean lure = applyLure != null ? applyLure : false;

        return ctx -> {
            if (ctx.event() instanceof PlayerFishEvent e) {
                FishHook hook = e.getHook();
                ActionUtils.runSync(ctx.plugin(), () -> {
                    hook.setMinWaitTime(minTicks);
                    hook.setMaxWaitTime(maxTicks);
                    hook.setApplyLure(lure);
                });
            }
            return ActionResult.ALLOW;
        };
    }
}
