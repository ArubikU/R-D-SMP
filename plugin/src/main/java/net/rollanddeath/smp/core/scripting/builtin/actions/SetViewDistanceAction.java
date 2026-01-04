package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.entity.Player;

final class SetViewDistanceAction {
    private SetViewDistanceAction() {
    }

    static void register() {
        ActionRegistrar.register("set_view_distance", SetViewDistanceAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Integer chunks = Resolvers.integer(null, raw, "chunks");
        if (chunks == null || chunks < 2) return null;
        
        return ctx -> {
            Player p = ctx.player();
            if (p != null) {
                p.setViewDistance(chunks);
            }
            return ActionResult.ALLOW;
        };
    }
}
