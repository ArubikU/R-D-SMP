package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

final class AggroNearbyCreaturesAction {
    private AggroNearbyCreaturesAction() {}

    static void register() {
        ActionRegistrar.register("aggro_nearby_creatures", AggroNearbyCreaturesAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Integer radius = Resolvers.integer(null, raw, "radius");
        Integer radiusY = Resolvers.integer(null, raw, "radius_y", "y_radius", "radiusy");
        int r = radius != null ? Math.max(1, radius) : 30;
        int ry = radiusY != null ? Math.max(1, radiusY) : 10;
        boolean onlyIfNoTarget = raw.get("only_if_no_target") instanceof Boolean b ? b : true;

        return ctx -> {
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            
            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity e : player.getNearbyEntities(r, ry, r)) {
                    if (e instanceof Creature c) {
                        if (onlyIfNoTarget && c.getTarget() != null) continue;
                        c.setTarget(player);
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
