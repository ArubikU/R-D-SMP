package net.rollanddeath.smp.core.modifiers.scripted;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.impl.EternalTorch;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Servicio reutilizable para la lógica de "Sombra Persistente" desde el motor scripted.
 */
public final class PersistentShadowService implements Listener {

    private static final long DEFAULT_INTERVAL_MS = 300_000L;
    private static final int DEFAULT_CHECK_PERIOD_TICKS = 100; // 5s

    private final JavaPlugin plugin;
    private final Map<ChunkKey, ChunkTorchData> chunkTorches = new HashMap<>();

    private EternalTorch eternalTorch;
    private BukkitRunnable task;
    private boolean running;

    private long intervalMs = DEFAULT_INTERVAL_MS;
    private int checkPeriodTicks = DEFAULT_CHECK_PERIOD_TICKS;

    public PersistentShadowService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void start(long intervalMs, int checkPeriodTicks) {
        if (intervalMs > 0) this.intervalMs = intervalMs;
        if (checkPeriodTicks > 0) this.checkPeriodTicks = checkPeriodTicks;

        if (running) {
            // Ya está activo: reiniciamos la tarea con los nuevos parámetros.
            if (task != null) task.cancel();
            startTask();
            return;
        }

        running = true;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        startTask();

        // Pobla el estado inicial.
        for (World world : plugin.getServer().getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                scanChunk(chunk);
            }
        }
    }

    public void stop() {
        if (!running) return;
        running = false;

        if (task != null) {
            task.cancel();
            task = null;
        }
        chunkTorches.clear();

        HandlerList.unregisterAll(this);
    }

    private void startTask() {
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

                    data.nextExtinguishAt = now + intervalMs;
                    if (data.torches.isEmpty()) {
                        it.remove();
                    }
                }
            }
        };

        task.runTaskTimer(plugin, 0L, checkPeriodTicks);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!running || event.isCancelled()) return;

        Block block = event.getBlockPlaced();
        if (block == null) return;

        if (!isTorch(block.getType())) return;

        ChunkKey key = ChunkKey.of(block.getChunk());
        BlockPosition position = BlockPosition.of(block.getLocation());
        ChunkTorchData data = chunkTorches.computeIfAbsent(key, k -> new ChunkTorchData(System.currentTimeMillis() + intervalMs));
        data.torches.add(position);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (!isEternal(block)) return;

            ChunkTorchData stored = chunkTorches.get(key);
            if (stored == null) return;

            stored.torches.remove(position);
            if (stored.torches.isEmpty()) {
                chunkTorches.remove(key);
            }
        });
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!running) return;

        Block block = event.getBlock();
        if (block == null || !isTorch(block.getType())) return;

        BlockPosition position = BlockPosition.of(block.getLocation());

        ChunkKey key = ChunkKey.of(block.getChunk());
        ChunkTorchData data = chunkTorches.get(key);
        if (data != null) {
            data.torches.remove(position);
            if (data.torches.isEmpty()) {
                chunkTorches.remove(key);
            }
        }

        if (getEternalTorch() == null) return;
        plugin.getServer().getScheduler().runTask(plugin, () -> forgetIfEternal(position));
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (!running) return;
        scanChunk(event.getChunk());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (!running) return;
        chunkTorches.remove(ChunkKey.of(event.getChunk()));
    }

    private void scanChunk(Chunk chunk) {
        if (chunk == null) return;

        ChunkKey key = ChunkKey.of(chunk);
        ChunkTorchData data = new ChunkTorchData(System.currentTimeMillis() + intervalMs);
        int minY = chunk.getWorld().getMinHeight();
        int maxY = chunk.getWorld().getMaxHeight();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    Block block = chunk.getBlock(x, y, z);
                    if (!isTorch(block.getType())) continue;
                    if (isEternal(block)) continue;
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
        return type == Material.TORCH
            || type == Material.WALL_TORCH
            || type == Material.REDSTONE_TORCH
            || type == Material.REDSTONE_WALL_TORCH
            || type == Material.SOUL_TORCH
            || type == Material.SOUL_WALL_TORCH;
    }

    private boolean isEternal(Block block) {
        EternalTorch torch = getEternalTorch();
        return torch != null && torch.isTrackedTorch(block);
    }

    private void forgetIfEternal(BlockPosition pos) {
        EternalTorch torch = getEternalTorch();
        if (torch == null) return;

        Block block = pos.toBlock(plugin);
        if (block == null) return;

        torch.forgetTorch(block.getLocation());
    }

    private EternalTorch getEternalTorch() {
        if (eternalTorch == null && plugin instanceof RollAndDeathSMP smp) {
            if (smp.getItemManager().getItem("ETERNAL_TORCH") instanceof EternalTorch torch) {
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

    private static final class ChunkTorchData {
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
            World world = plugin.getServer().getWorld(worldId);
            if (world == null) return null;

            int chunkX = x >> 4;
            int chunkZ = z >> 4;
            if (!world.isChunkLoaded(chunkX, chunkZ)) return null;

            return world.getBlockAt(x, y, z);
        }
    }
}
