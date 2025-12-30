package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.EntitySpawnEvent;

final class SetEventSkeletonBowIntervalAction {
    private SetEventSkeletonBowIntervalAction() {}

    static void register() {
        ActionRegistrar.register("set_event_skeleton_bow_interval", SetEventSkeletonBowIntervalAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Integer interval = Resolvers.integer(null, raw, "interval");
        if (interval == null || interval < 1) return null;
        final int i = interval;

        return ctx -> {
            EntitySpawnEvent ese = ctx.nativeEvent(EntitySpawnEvent.class);
            if (ese == null) return ActionResult.ALLOW;
            if (!(ese.getEntity() instanceof Skeleton skeleton)) return ActionResult.ALLOW;

            ActionUtils.runSync(ctx.plugin(), () -> ActionUtils.trySetSkeletonBowInterval(skeleton, i));
            return ActionResult.ALLOW;
        };
    }
}
