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
        int r = radius != null ? Math.max(0, radius) : 4;
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

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (int x = -r; x <= r; x++) {
                    for (int y = -r; y <= r; y++) {
                        for (int z = -r; z <= r; z++) {
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
