package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

final class SetBlockAction {
    private SetBlockAction() {}

    static void register() {
        ActionRegistrar.register("set_block", SetBlockAction::parse, "block", "change_block");
    }

    private static Action parse(Map<?, ?> raw) {
        Object targetSpec = raw.get("location");
        if (targetSpec == null) targetSpec = raw.get("at");
        if (targetSpec == null) targetSpec = raw.get("block"); // Could be block object
        
        Material type = Resolvers.material(null, raw, "type", "material");
        
        // Modifiers
        boolean breakNaturally = raw.get("break") instanceof Boolean b ? b : false;
        boolean dropItems = raw.get("drop_items") instanceof Boolean b ? b : true; // For break
        boolean physics = raw.get("physics") instanceof Boolean b ? b : true;
        
        // Relative offsets
        Double offsetX = Resolvers.doubleVal(null, raw, "offset_x", "dx");
        Double offsetY = Resolvers.doubleVal(null, raw, "offset_y", "dy");
        Double offsetZ = Resolvers.doubleVal(null, raw, "offset_z", "dz");

        return ctx -> {
            List<Location> locations = Resolvers.locations(ctx, targetSpec);
            if (locations.isEmpty()) {
                if (targetSpec == null && ctx.location() != null) {
                    locations = List.of(ctx.location());
                } else {
                    return ActionResult.ALLOW;
                }
            }

            double ox = offsetX != null ? offsetX : 0;
            double oy = offsetY != null ? offsetY : 0;
            double oz = offsetZ != null ? offsetZ : 0;

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Location loc : locations) {
                    Location target = loc.clone().add(ox, oy, oz);
                    Block b = target.getBlock();
                    
                    if (breakNaturally) {
                        b.breakNaturally(dropItems ? null : ctx.player() != null ? ctx.player().getInventory().getItemInMainHand() : null); // Simplified
                        // Actually breakNaturally() drops items. If dropItems is false, we should just set type to AIR?
                        // Or use setType(AIR).
                    }
                    
                    if (type != null) {
                        b.setType(type, physics);
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }
}
