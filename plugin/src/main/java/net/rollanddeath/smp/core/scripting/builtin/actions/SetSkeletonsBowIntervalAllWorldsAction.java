package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Skeleton;

final class SetSkeletonsBowIntervalAllWorldsAction {
    private SetSkeletonsBowIntervalAllWorldsAction() {}

    static void register() {
        ActionRegistrar.register("set_skeletons_bow_interval_all_worlds", SetSkeletonsBowIntervalAllWorldsAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Integer interval = Resolvers.integer(null, raw, "interval");
        if (interval == null || interval < 1) return null;
        final int i = interval;

        return ctx -> {
            ActionUtils.runSync(ctx.plugin(), () -> {
                for (World w : Bukkit.getWorlds()) {
                    for (org.bukkit.entity.Entity e : w.getEntities()) {
                        if (e instanceof Skeleton s) {
                            ActionUtils.trySetSkeletonBowInterval(s, i);
                        }
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
