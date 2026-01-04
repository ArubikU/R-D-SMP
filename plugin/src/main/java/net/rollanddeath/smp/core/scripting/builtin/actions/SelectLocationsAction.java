package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

final class SelectLocationsAction {
    private SelectLocationsAction() {}

    static void register() {
        ActionRegistrar.register("select_locations", SelectLocationsAction::parse, "select_location", "find_location", "find_locations");
    }

    private static Action parse(Map<?, ?> raw) {
        String storeKey = Resolvers.string(null, raw, "store", "var", "key");
        if (storeKey == null || storeKey.isBlank()) return null;

        Object sourceSpecRaw = raw.get("source");
        if (sourceSpecRaw == null) sourceSpecRaw = raw.get("origin");
        if (sourceSpecRaw == null) sourceSpecRaw = raw.get("center");
        final Object sourceSpec = sourceSpecRaw;
        
        Double radius = Resolvers.doubleVal(null, raw, "radius", "r", "spread");
        Integer count = Resolvers.integer(null, raw, "count", "amount", "limit");
        
        // Modifiers
        boolean topBlock = raw.get("top_block") instanceof Boolean b ? b : false;
        boolean ground = raw.get("ground") instanceof Boolean b ? b : false; // Similar to top_block but maybe checks solid?
        
        Double offsetX = Resolvers.doubleVal(null, raw, "offset_x", "dx");
        Double offsetY = Resolvers.doubleVal(null, raw, "offset_y", "dy");
        Double offsetZ = Resolvers.doubleVal(null, raw, "offset_z", "dz");

        return ctx -> {
            List<Location> resolvedOrigins = Resolvers.locations(ctx, sourceSpec);
            final List<Location> origins;
            if (resolvedOrigins.isEmpty()) {
                if (sourceSpec == null && ctx.location() != null) {
                    origins = List.of(ctx.location());
                } else {
                    return ActionResult.ALLOW;
                }
            } else {
                origins = resolvedOrigins;
            }

            List<Location> results = new ArrayList<>();
            int limit = count != null ? Math.max(1, count) : 1;
            double r = radius != null ? Math.max(0, radius) : 0;
            
            double ox = offsetX != null ? offsetX : 0;
            double oy = offsetY != null ? offsetY : 0;
            double oz = offsetZ != null ? offsetZ : 0;

            for (Location origin : origins) {
                if (results.size() >= limit && count != null) break; // Global limit? Or per origin? Assuming global for now.
                
                Location base = origin.clone().add(ox, oy, oz);
                
                if (r > 0) {
                    // Random spread around base
                    // If count > 1, maybe generate multiple points around this origin?
                    // If count is not specified (null), maybe just 1 point?
                    // If count is specified, we might want to generate 'count' points TOTAL or per origin.
                    // Let's assume if count is specified, we try to fill up to count.
                    
                    int pointsToGen = (count != null) ? (limit - results.size()) : 1;
                    // If multiple origins, we distribute? Or just iterate.
                    // Simple logic: generate 1 point per origin unless count forces more.
                    
                    if (count == null) pointsToGen = 1;
                    
                    for (int i = 0; i < pointsToGen; i++) {
                        double dx = (ThreadLocalRandom.current().nextDouble() * 2 - 1) * r;
                        double dz = (ThreadLocalRandom.current().nextDouble() * 2 - 1) * r;
                        // For 3D spread? Usually radius implies horizontal, but maybe spherical?
                        // Let's stick to horizontal cylinder for now unless specified.
                        
                        Location candidate = base.clone().add(dx, 0, dz);
                        if (topBlock || ground) {
                            candidate = getTopBlock(candidate);
                        }
                        results.add(candidate);
                    }
                } else {
                    // Exact location
                    Location candidate = base;
                    if (topBlock || ground) {
                        candidate = getTopBlock(candidate);
                    }
                    results.add(candidate);
                }
            }
            
            // Store result
            if (count != null && count > 1) {
                ctx.setGenericVarCompat(storeKey, results);
            } else {
                ctx.setGenericVarCompat(storeKey, results.isEmpty() ? null : results.get(0));
            }

            return ActionResult.ALLOW;
        };
    }
    
    private static Location getTopBlock(Location loc) {
        World w = loc.getWorld();
        if (w == null) return loc;
        Block b = w.getHighestBlockAt(loc);
        return b.getLocation().add(0.5, 1, 0.5); // Center on top
    }
}
