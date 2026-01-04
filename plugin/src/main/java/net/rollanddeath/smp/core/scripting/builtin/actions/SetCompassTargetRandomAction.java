package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.concurrent.ThreadLocalRandom;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Location;
import org.bukkit.entity.Player;

final class SetCompassTargetRandomAction {
    private SetCompassTargetRandomAction() {
    }

    static void register() {
        ActionRegistrar.register("set_compass_target_random", SetCompassTargetRandomAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Integer minX = Resolvers.integer(null, raw, "min_x");
        Integer maxX = Resolvers.integer(null, raw, "max_x");
        Integer minZ = Resolvers.integer(null, raw, "min_z");
        Integer maxZ = Resolvers.integer(null, raw, "max_z");
        Integer y = Resolvers.integer(null, raw, "y");
        if (minX == null || maxX == null || minZ == null || maxZ == null) return null;
        
        int fMinX = minX;
        int fMaxX = maxX;
        int fMinZ = minZ;
        int fMaxZ = maxZ;
        int fY = y != null ? y : 64;

        return ctx -> {
            Player p = ctx.player();
            if (p == null) return ActionResult.ALLOW;

            int x = ThreadLocalRandom.current().nextInt(fMinX, fMaxX + 1);
            int z = ThreadLocalRandom.current().nextInt(fMinZ, fMaxZ + 1);
            Location loc = new Location(p.getWorld(), x, fY, z);

            p.setCompassTarget(loc);
            return ActionResult.ALLOW;
        };
    }
}
