package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Location;

final class ExplodeAction {
    private ExplodeAction() {}

    static void register() {
        ActionRegistrar.register("explode", ExplodeAction::parse, "explosion", "create_explosion");
    }

    private static Action parse(Map<?, ?> raw) {
        Object locationSpec = raw.get("location");
        if (locationSpec == null) locationSpec = raw.get("where");
        if (locationSpec == null) locationSpec = raw.get("target");
        
        Double power = Resolvers.doubleVal(null, raw, "power", "yield", "size");
        Boolean setFire = Resolvers.bool(null, raw, "fire", "set_fire", "incendiary");
        Boolean breakBlocks = Resolvers.bool(null, raw, "break_blocks", "break", "destroy");
        
        if (power == null) power = 4.0;
        if (setFire == null) setFire = false;
        if (breakBlocks == null) breakBlocks = true;

        final Double finalPower = power;
        final Boolean finalSetFire = setFire;
        final Boolean finalBreakBlocks = breakBlocks;
        final Object finalLocationSpec = locationSpec;

        return ctx -> {
            List<Location> locs = Resolvers.locations(ctx, finalLocationSpec);
            if (locs.isEmpty()) {
                if (finalLocationSpec == null && ctx.location() != null) {
                    locs = List.of(ctx.location());
                } else {
                    return ActionResult.ALLOW;
                }
            }

            final List<Location> finalLocs = locs;
            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Location loc : finalLocs) {
                    if (loc != null && loc.getWorld() != null) {
                        loc.getWorld().createExplosion(loc, finalPower.floatValue(), finalSetFire, finalBreakBlocks);
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }
}
