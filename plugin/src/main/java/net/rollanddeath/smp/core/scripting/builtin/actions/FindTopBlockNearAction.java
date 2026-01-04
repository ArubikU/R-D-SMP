package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

final class FindTopBlockNearAction {
    private FindTopBlockNearAction() {}

    static void register() {
        ActionRegistrar.register("find_top_block_near", FindTopBlockNearAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String worldName = Resolvers.string(null, raw, "world");
        Double radius = Resolvers.doubleVal(null, raw, "radius");
        Integer attempts = Resolvers.integer(null, raw, "attempts");
        String storeKey = Resolvers.string(null, raw, "store_key", "store");
        
        if (storeKey == null) return null;
        
        double r = radius != null ? radius : 100;
        int att = attempts != null ? attempts : 10;

        return ctx -> {
            World w = worldName != null ? Bukkit.getWorld(worldName) : (ctx.location() != null ? ctx.location().getWorld() : null);
            if (w == null) return ActionResult.ALLOW;
            
            Location center = ctx.location();
            if (center == null) center = w.getSpawnLocation();
            
            Location found = null;
            
            for (int i = 0; i < att; i++) {
                double dx = (ThreadLocalRandom.current().nextDouble() * 2 - 1) * r;
                double dz = (ThreadLocalRandom.current().nextDouble() * 2 - 1) * r;
                
                int x = center.getBlockX() + (int)dx;
                int z = center.getBlockZ() + (int)dz;
                
                Block b = w.getHighestBlockAt(x, z);
                if (b.getType().isSolid()) {
                    found = b.getLocation();
                    break;
                }
            }
            
            if (found != null) {
                ctx.setGenericVarCompat(storeKey, found);
            }

            return ActionResult.ALLOW;
        };
    }
}
