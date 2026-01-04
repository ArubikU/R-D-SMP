package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

final class PullNearbyItemsAction {
    private PullNearbyItemsAction() {}

    static void register() {
        ActionRegistrar.register("pull_nearby_items", PullNearbyItemsAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Double radius = Resolvers.doubleVal(null, raw, "radius", "range");
        Double speed = Resolvers.doubleVal(null, raw, "speed");
        Boolean pullItems = Resolvers.bool(null, raw, "pull_items", "items");
        Boolean pullXp = Resolvers.bool(null, raw, "pull_xp", "xp");
        Boolean ignoreDelay = Resolvers.bool(null, raw, "ignore_pickup_delay");

        return ctx -> {
            Player p = ctx.player();
            if (p == null) return ActionResult.ALLOW;

            double r = radius != null ? radius : 5.0;
            double s = speed != null ? speed : 0.5;
            boolean items = pullItems != null ? pullItems : true;
            boolean xp = pullXp != null ? pullXp : true;
            boolean ignore = ignoreDelay != null ? ignoreDelay : false;

            ActionUtils.runSync(ctx.plugin(), () -> {
                List<Entity> nearby = p.getNearbyEntities(r, r, r);
                Location targetLoc = p.getLocation().add(0, 0.5, 0);

                for (Entity e : nearby) {
                    boolean valid = false;
                    if (items && e instanceof Item item) {
                        if (ignore) item.setPickupDelay(0);
                        valid = true;
                    } else if (xp && e instanceof ExperienceOrb) {
                        valid = true;
                    }

                    if (valid) {
                        Vector dir = targetLoc.toVector().subtract(e.getLocation().toVector()).normalize().multiply(s);
                        e.setVelocity(dir);
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }
}
