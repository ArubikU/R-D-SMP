package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

final class AggroNearbyMobsAction {
    private AggroNearbyMobsAction() {}

    static void register() {
        ActionRegistrar.register("aggro_nearby_mobs", AggroNearbyMobsAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String entityType = Resolvers.string(null, raw, "entity_type");
        if (entityType == null || entityType.isBlank()) return null;
        Integer radius = Resolvers.integer(null, raw, "radius");
        int r = radius != null ? Math.max(1, radius) : 32;
        boolean onlyIfNoTarget = raw.get("only_if_no_target") instanceof Boolean b ? b : true;

        return ctx -> {
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            
            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity e : player.getNearbyEntities(r, r, r)) {
                    if (e instanceof Mob m && m.getType().name().equalsIgnoreCase(entityType)) {
                        if (onlyIfNoTarget && m.getTarget() != null) continue;
                        m.setTarget(player);
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
