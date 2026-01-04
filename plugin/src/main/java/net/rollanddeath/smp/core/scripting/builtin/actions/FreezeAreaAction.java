package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

final class FreezeAreaAction {
    private FreezeAreaAction() {}

    static void register() {
        ActionRegistrar.register("freeze_area", FreezeAreaAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Object key = raw.get("where");
        if (key == null) key = raw.get("location");
        if (key == null) key = raw.get("key");
        if (key == null) key = raw.get("location_key");
        if (key == null) return null;

        Integer radius = Resolvers.integer(null, raw, "radius");
        String radiusKey = Resolvers.string(null, raw, "radius_key");
        int defaultR = radius != null ? Math.max(0, radius) : 4;
        
        Double airChance = Resolvers.doubleVal(null, raw, "air_chance");
        double p = airChance != null ? Math.max(0.0, Math.min(1.0, airChance)) : 0.3;
        
        String airMatName = Resolvers.string(null, raw, "air_material");
        String waterMatName = Resolvers.string(null, raw, "water_material");
        String iceMatName = Resolvers.string(null, raw, "ice_material");
        
        Material airMat = airMatName != null ? Material.getMaterial(airMatName) : Material.POWDER_SNOW;
        Material waterMat = waterMatName != null ? Material.getMaterial(waterMatName) : Material.WATER;
        Material iceMat = iceMatName != null ? Material.getMaterial(iceMatName) : Material.ICE;
        
        if (airMat == null) airMat = Material.POWDER_SNOW;
        if (waterMat == null) waterMat = Material.WATER;
        if (iceMat == null) iceMat = Material.ICE;

        final Object finalKey = key;
        final Material finalAirMat = airMat;
        final Material finalWaterMat = waterMat;
        final Material finalIceMat = iceMat;

        return ctx -> {
            Location loc = Resolvers.resolveLocation(ctx, finalKey);
            if (loc == null) return ActionResult.ALLOW;

            // Resolver radio dinámicamente
            int r = defaultR;
            if (radiusKey != null && !radiusKey.isBlank()) {
                Double dyn = Resolvers.doubleVal(ctx, radiusKey);
                if (dyn != null) r = (int) Math.round(dyn);
            }
            final int finalR = Math.max(0, r);

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (int x = -finalR; x <= finalR; x++) {
                    for (int y = -finalR; y <= finalR; y++) {
                        for (int z = -finalR; z <= finalR; z++) {
                            Block b = loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
                            if (b.getType() == Material.AIR) {
                                if (Math.random() < p) {
                                    b.setType(finalAirMat);
                                }
                            } else if (b.getType() == finalWaterMat) {
                                b.setType(finalIceMat);
                            }
                        }
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
