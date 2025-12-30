package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Location;
import org.bukkit.World;

final class LightningAction {
    private LightningAction() {}

    static void register() {
        ActionRegistrar.register("lightning", LightningAction::parse, "strike_lightning", "lightning_strike", "thunder");
    }

    private static Action parse(Map<?, ?> raw) {
        Object targetSpec = raw.get("target");
        if (targetSpec == null) targetSpec = raw.get("location");
        if (targetSpec == null) targetSpec = raw.get("entity");
        
        boolean effectOnly = raw.get("effect_only") instanceof Boolean b ? b : false;

        return ctx -> {
            List<Location> locations = Resolvers.locations(ctx, targetSpec);
            if (locations.isEmpty()) {
                if (targetSpec == null && ctx.location() != null) {
                    locations = List.of(ctx.location());
                } else {
                    return ActionResult.ALLOW;
                }
            }

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Location loc : locations) {
                    World w = loc.getWorld();
                    if (w == null) continue;
                    
                    if (effectOnly) {
                        w.strikeLightningEffect(loc);
                    } else {
                        w.strikeLightning(loc);
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }
}
