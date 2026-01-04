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
        ActionRegistrar.register("teleport", TeleportAction::parse, "tp", "teleport_entity", "teleport_player", "teleport_player_to_key");
    }

    private static Action parse(Map<?, ?> raw) {
        Object targetSpecRaw = raw.get("target");
        if (targetSpecRaw == null) targetSpecRaw = raw.get("entity");
        final Object targetSpec = targetSpecRaw;
        
        Object locationSpecRaw = raw.get("to");
        if (locationSpecRaw == null) locationSpecRaw = raw.get("location");
        if (locationSpecRaw == null) locationSpecRaw = raw.get("destination");
        final Object locationSpec = locationSpecRaw;
        
        return ctx -> {
            List<Entity> resolved = Resolvers.entities(ctx, targetSpec);
            final List<Entity> targets;
            if (resolved.isEmpty()) {
                if (targetSpec == null && ctx.subject() != null) {
                    targets = List.of(ctx.subject());
                } else {
                    return ActionResult.ALLOW;
                }
            } else {
                targets = resolved;
            }

            Location loc = Resolvers.location(ctx, locationSpec);
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
