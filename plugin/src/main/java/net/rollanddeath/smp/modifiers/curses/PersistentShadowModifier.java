package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PersistentShadowModifier extends Modifier {

    private final Map<Location, Long> torches = new HashMap<>();
    private BukkitRunnable task;

    public PersistentShadowModifier(JavaPlugin plugin) {
        super(plugin, "Sombra Persistente", ModifierType.CURSE, "Las antorchas se apagan solas tras 5 minutos.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                Iterator<Map.Entry<Location, Long>> it = torches.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Location, Long> entry = it.next();
                    if (now - entry.getValue() > 300000) { // 5 minutes
                        Block block = entry.getKey().getBlock();
                        if (block.getType() == Material.TORCH || block.getType() == Material.WALL_TORCH || block.getType() == Material.SOUL_TORCH || block.getType() == Material.SOUL_WALL_TORCH) {
                            block.setType(Material.AIR);
                            block.getWorld().playEffect(block.getLocation(), org.bukkit.Effect.EXTINGUISH, 0);
                        }
                        it.remove();
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 100L); // Check every 5 seconds
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null) task.cancel();
        torches.clear();
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Material type = event.getBlock().getType();
        if (type == Material.TORCH || type == Material.WALL_TORCH || type == Material.SOUL_TORCH || type == Material.SOUL_WALL_TORCH) {
            torches.put(event.getBlock().getLocation(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        torches.remove(event.getBlock().getLocation());
    }
}
