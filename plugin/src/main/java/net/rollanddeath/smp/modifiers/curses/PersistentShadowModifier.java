package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItemType;
import net.rollanddeath.smp.core.items.impl.EternalTorch;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PersistentShadowModifier extends Modifier {

    private static final long INTERVAL_MS = 300_000L;

    private final Map<ChunkKey, ChunkTorchData> chunkTorches = new HashMap<>();
    private EternalTorch eternalTorch;
    private BukkitRunnable task;

    public PersistentShadowModifier(JavaPlugin plugin) {
        super(plugin, "Sombra Persistente", ModifierType.CURSE, "Las antorchas se apagan solas tras 5 minutos.");
        this.eternalTorch = null;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                Iterator<Map.Entry<ChunkKey, ChunkTorchData>> it = chunkTorches.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<ChunkKey, ChunkTorchData> entry = it.next();
                    ChunkTorchData data = entry.getValue();
                    if (now < data.nextExtinguishAt) {
                        continue;
                    }

                    Iterator<BlockPosition> torchIt = data.torches.iterator();
                    while (torchIt.hasNext()) {
                        BlockPosition pos = torchIt.next();
                        Block block = pos.toBlock(plugin);
                        if (block == null || !isTorch(block.getType())) {
                            torchIt.remove();
                            forgetIfEternal(pos);
                            continue;
                        }

                        if (isEternal(block)) {
                            torchIt.remove();
                            continue;
                        }

                        torchIt.remove();
                        block.setType(Material.AIR, false);
                        block.getWorld().playEffect(block.getLocation(), org.bukkit.Effect.EXTINGUISH, 0);
                        block.getWorld().dropItemNaturally(block.getLocation(), new org.bukkit.inventory.ItemStack(Material.STICK));
                    }

                    data.nextExtinguishAt = now + INTERVAL_MS;
                    if (data.torches.isEmpty()) {
                        it.remove();
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 100L); // Check every 5 seconds

        for (World world : plugin.getServer().getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                scanChunk(chunk);
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null) task.cancel();
        chunkTorches.clear();
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Block block = event.getBlockPlaced();
        Material type = block.getType();
        if (isTorch(type)) {
            ChunkKey key = ChunkKey.of(block.getChunk());
            BlockPosition position = BlockPosition.of(block.getLocation());
            ChunkTorchData data = chunkTorches.computeIfAbsent(key, k -> new ChunkTorchData(System.currentTimeMillis() + INTERVAL_MS));
            data.torches.add(position);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (!isEternal(block)) {
                    return;
                }
                ChunkTorchData stored = chunkTorches.get(key);
                if (stored == null) {
                    return;
                }
                stored.torches.remove(position);
                if (stored.torches.isEmpty()) {
                    chunkTorches.remove(key);
                }
            });
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!isTorch(block.getType())) {
            return;
        }

        BlockPosition position = BlockPosition.of(block.getLocation());

        ChunkKey key = ChunkKey.of(block.getChunk());
        ChunkTorchData data = chunkTorches.get(key);
        if (data != null) {
            data.torches.remove(position);
            if (data.torches.isEmpty()) {
                chunkTorches.remove(key);
            }
        }

        if (getEternalTorch() == null) {
            return;
        }
        plugin.getServer().getScheduler().runTask(plugin, () -> forgetIfEternal(position));
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        scanChunk(event.getChunk());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        chunkTorches.remove(ChunkKey.of(event.getChunk()));
    }

    private void scanChunk(Chunk chunk) {
        ChunkKey key = ChunkKey.of(chunk);
        ChunkTorchData data = new ChunkTorchData(System.currentTimeMillis() + INTERVAL_MS);
        int minY = chunk.getWorld().getMinHeight();
        int maxY = chunk.getWorld().getMaxHeight();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    Block block = chunk.getBlock(x, y, z);
                    if (!isTorch(block.getType())) {
                        continue;
                    }
                    if (isEternal(block)) {
                        continue;
                    }
                    data.torches.add(BlockPosition.of(block.getLocation()));
                }
            }
        }

        if (data.torches.isEmpty()) {
            chunkTorches.remove(key);
        } else {
            chunkTorches.put(key, data);
        }
    }

    private boolean isTorch(Material type) {
        return type == Material.TORCH || type == Material.WALL_TORCH || type == Material.SOUL_TORCH || type == Material.SOUL_WALL_TORCH;
    }

    private boolean isEternal(Block block) {
        EternalTorch torch = getEternalTorch();
        return torch != null && torch.isTrackedTorch(block);
    }

    private void forgetIfEternal(BlockPosition pos) {
        EternalTorch torch = getEternalTorch();
        if (torch == null) {
            return;
        }
        Block block = pos.toBlock(plugin);
        if (block == null) {
            return;
        }
        torch.forgetTorch(block.getLocation());
    }

    private EternalTorch getEternalTorch() {
        if (eternalTorch == null && plugin instanceof RollAndDeathSMP smp) {
            if (smp.getItemManager().getItem(CustomItemType.ETERNAL_TORCH) instanceof EternalTorch torch) {
                eternalTorch = torch;
            }
        }
        return eternalTorch;
    }

    private record ChunkKey(UUID worldId, int x, int z) {

        static ChunkKey of(org.bukkit.Chunk chunk) {
            return new ChunkKey(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ());
        }
    }

    private static class ChunkTorchData {
        long nextExtinguishAt;
        final Set<BlockPosition> torches = new HashSet<>();

        ChunkTorchData(long nextExtinguishAt) {
            this.nextExtinguishAt = nextExtinguishAt;
        }
    }

    private record BlockPosition(UUID worldId, int x, int y, int z) {

        static BlockPosition of(Location location) {
            return new BlockPosition(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        }

        Block toBlock(JavaPlugin plugin) {
            org.bukkit.World world = plugin.getServer().getWorld(worldId);
            if (world == null) {
                return null;
            }
            int chunkX = x >> 4;
            int chunkZ = z >> 4;
            if (!world.isChunkLoaded(chunkX, chunkZ)) {
                return null;
            }
            return world.getBlockAt(x, y, z);
        }
    }
}
