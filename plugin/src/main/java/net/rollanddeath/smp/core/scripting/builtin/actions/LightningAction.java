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
        ActionRegistrar.register("lightning", LightningAction::parse, "strike_lightning", "lightning_strike", "thunder", "strike_lightning_effect_at");
    }

    private static Action parse(Map<?, ?> raw) {
        Object targetSpecRaw = raw.get("target");
        if (targetSpecRaw == null) targetSpecRaw = raw.get("location");
        if (targetSpecRaw == null) targetSpecRaw = raw.get("entity");
        final Object targetSpec = targetSpecRaw;
        
        boolean effectOnly = raw.get("effect_only") instanceof Boolean b ? b : false;

        return ctx -> {
            List<Location> resolvedLocations = Resolvers.locations(ctx, targetSpec);
            final List<Location> locations;
            if (resolvedLocations.isEmpty()) {
                if (targetSpec == null && ctx.location() != null) {
                    locations = List.of(ctx.location());
                } else {
                    return ActionResult.ALLOW;
                }
            } else {
                locations = resolvedLocations;
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
