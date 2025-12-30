package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

final class TeleportAction {
    private TeleportAction() {}

    static void register() {
        ActionRegistrar.register("teleport", TeleportAction::parse, "tp", "teleport_entity", "teleport_player");
    }

    private static Action parse(Map<?, ?> raw) {
        Object targetSpec = raw.get("target");
        if (targetSpec == null) targetSpec = raw.get("entity");
        
        Object locationSpec = raw.get("to");
        if (locationSpec == null) locationSpec = raw.get("location");
        if (locationSpec == null) locationSpec = raw.get("destination");
        
        return ctx -> {
            List<Entity> targets = Resolvers.entities(ctx, targetSpec);
            if (targets.isEmpty()) {
                if (targetSpec == null && ctx.subject() != null) {
                    targets = List.of(ctx.subject());
                } else {
                    return ActionResult.ALLOW;
                }
            }

            Location loc = Resolvers.location(locationSpec, ctx);
            if (loc == null) return ActionResult.ALLOW;

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity e : targets) {
                    e.teleport(loc);
                }
            });

            return ActionResult.ALLOW;
        };
    }
}
