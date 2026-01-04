package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.concurrent.ThreadLocalRandom;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

final class AddVelocityRandomAction {
    private AddVelocityRandomAction() {
    }

    static void register() {
        ActionRegistrar.register("add_velocity_random", AddVelocityRandomAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Double minX = Resolvers.doubleVal(null, raw, "min_x");
        Double maxX = Resolvers.doubleVal(null, raw, "max_x");
        Double minY = Resolvers.doubleVal(null, raw, "min_y");
        Double maxY = Resolvers.doubleVal(null, raw, "max_y");
        Double minZ = Resolvers.doubleVal(null, raw, "min_z");
        Double maxZ = Resolvers.doubleVal(null, raw, "max_z");
        if (minX == null && maxX == null && minY == null && maxY == null && minZ == null && maxZ == null) return null;
        
        double fMinX = minX != null ? minX : 0.0;
        double fMaxX = maxX != null ? maxX : fMinX;
        double fMinY = minY != null ? minY : 0.0;
        double fMaxY = maxY != null ? maxY : fMinY;
        double fMinZ = minZ != null ? minZ : 0.0;
        double fMaxZ = maxZ != null ? maxZ : fMinZ;

        return ctx -> {
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            double x = ThreadLocalRandom.current().nextDouble(Math.min(fMinX, fMaxX), Math.max(fMinX, fMaxX));
            double y = ThreadLocalRandom.current().nextDouble(Math.min(fMinY, fMaxY), Math.max(fMinY, fMaxY));
            double z = ThreadLocalRandom.current().nextDouble(Math.min(fMinZ, fMaxZ), Math.max(fMinZ, fMaxZ));

            Vector add = new Vector(x, y, z);
            ActionUtils.runSync(ctx.plugin(), () -> player.setVelocity(player.getVelocity().add(add)));
            return ActionResult.ALLOW;
        };
    }
}
