package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class MasterLumberjackModifier extends Modifier {

    public MasterLumberjackModifier(JavaPlugin plugin) {
        super(plugin, "Leñador Maestro", ModifierType.BLESSING, "Talar un bloque rompe todo el árbol.");
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (isLog(block.getType())) {
            // Avoid infinite recursion if player breaks a log that is part of a huge forest
            // We limit to 200 blocks
            breakTree(block);
        }
    }

    private boolean isLog(Material type) {
        return type.name().endsWith("_LOG");
    }

    private void breakTree(Block startBlock) {
        Queue<Block> queue = new LinkedList<>();
        Set<Block> visited = new HashSet<>();
        
        queue.add(startBlock);
        visited.add(startBlock);
        
        int count = 0;
        while (!queue.isEmpty() && count < 200) {
            Block current = queue.poll();
            current.breakNaturally();
            count++;

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        
                        Block relative = current.getRelative(x, y, z);
                        if (!visited.contains(relative) && isLog(relative.getType())) {
                            visited.add(relative);
                            queue.add(relative);
                        }
                    }
                }
            }
        }
    }
}
