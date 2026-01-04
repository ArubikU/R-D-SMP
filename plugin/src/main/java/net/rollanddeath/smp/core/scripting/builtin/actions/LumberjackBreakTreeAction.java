package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

final class LumberjackBreakTreeAction {
    private LumberjackBreakTreeAction() {}

    static void register() {
        ActionRegistrar.register("lumberjack_break_tree", LumberjackBreakTreeAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Integer limit = Resolvers.integer(null, raw, "limit");
        int max = limit != null ? limit : 200;

        return ctx -> {
            if (ctx.event() instanceof BlockBreakEvent e) {
                Block start = e.getBlock();
                if (!Tag.LOGS.isTagged(start.getType())) return ActionResult.ALLOW;
                
                ItemStack tool = ctx.player().getInventory().getItemInMainHand();
                
                ActionUtils.runSync(ctx.plugin(), () -> {
                    breakTree(start, tool, max);
                });
            }
            return ActionResult.ALLOW;
        };
    }

    private static void breakTree(Block start, ItemStack tool, int limit) {
        Set<Block> visited = new HashSet<>();
        Queue<Block> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);
        
        int broken = 0;
        
        while (!queue.isEmpty() && broken < limit) {
            Block current = queue.poll();
            
            // Break it (except start, which is already broken by event? No, event happens before break usually? 
            // Or we can break it manually. If event is not cancelled, start block breaks naturally.
            // But we want to break connected logs.
            
            if (current != start) {
                current.breakNaturally(tool);
                broken++;
            }
            
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        
                        Block rel = current.getRelative(x, y, z);
                        if (!visited.contains(rel) && Tag.LOGS.isTagged(rel.getType())) {
                            visited.add(rel);
                            queue.add(rel);
                        }
                    }
                }
            }
        }
    }
}
