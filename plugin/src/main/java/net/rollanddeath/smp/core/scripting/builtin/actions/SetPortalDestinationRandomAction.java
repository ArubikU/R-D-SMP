package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.concurrent.ThreadLocalRandom;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Location;
import org.bukkit.World;

final class SetPortalDestinationRandomAction {
    private SetPortalDestinationRandomAction() {
    }

    static void register() {
        ActionRegistrar.register("set_portal_destination_random", SetPortalDestinationRandomAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Integer minX = Resolvers.integer(null, raw, "min_x");
        Integer maxX = Resolvers.integer(null, raw, "max_x");
        Integer minZ = Resolvers.integer(null, raw, "min_z");
        Integer maxZ = Resolvers.integer(null, raw, "max_z");
        Integer y = Resolvers.integer(null, raw, "y");
        boolean useHighest = raw.get("use_highest_block") instanceof Boolean b ? b : true;
        if (minX == null || maxX == null || minZ == null || maxZ == null) return null;
        
        int fMinX = minX;
        int fMaxX = maxX;
        int fMinZ = minZ;
        int fMaxZ = maxZ;
        
        return ctx -> {
            org.bukkit.event.entity.EntityPortalEvent ev = ctx.nativeEvent(org.bukkit.event.entity.EntityPortalEvent.class);
            if (ev == null) return ActionResult.ALLOW;

            int x = ThreadLocalRandom.current().nextInt(fMinX, fMaxX + 1);
            int z = ThreadLocalRandom.current().nextInt(fMinZ, fMaxZ + 1);
            
            Location to = ev.getTo();
            World targetWorld = (to != null) ? to.getWorld() : ev.getFrom().getWorld();
            
            int finalY = (y != null) ? y : 64;
            if (useHighest) {
                finalY = targetWorld.getHighestBlockYAt(x, z);
            }

            Location dest = new Location(targetWorld, x, finalY, z);
            ev.setTo(dest);
            return ActionResult.ALLOW;
        };
    }
}
