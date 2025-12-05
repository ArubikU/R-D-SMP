package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EternalTorch extends CustomItem {

    private final Set<BlockPosition> placedTorches = ConcurrentHashMap.newKeySet();
    private final File dataFile;

    public EternalTorch(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.ETERNAL_TORCH);
        this.dataFile = new File(plugin.getDataFolder(), "eternal_torches.yml");
        loadPersistedTorches();
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.TORCH);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Resiste la sombra persistente");
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (isItem(event.getItemInHand())) {
            placedTorches.add(BlockPosition.of(event.getBlockPlaced().getLocation()));
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (event.isCancelled()) {
            return;
        }

        BlockPosition position = BlockPosition.of(block.getLocation());
        if (!placedTorches.remove(position)) {
            return;
        }

        event.setDropItems(false);
        block.getWorld().dropItemNaturally(block.getLocation(), getItemStack());
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (!event.getPlugin().equals(plugin)) {
            return;
        }
        savePersistedTorches();
    }

    public boolean isTrackedTorch(Block block) {
        return placedTorches.contains(BlockPosition.of(block.getLocation()));
    }

    public void forgetTorch(Location location) {
        placedTorches.remove(BlockPosition.of(location));
    }

    private record BlockPosition(UUID worldId, int x, int y, int z) {

        static BlockPosition of(Location location) {
            return new BlockPosition(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        }

        String serialize() {
            return worldId + ":" + x + ":" + y + ":" + z;
        }

        static BlockPosition deserialize(String value) {
            String[] parts = value.split(":", 4);
            if (parts.length != 4) {
                return null;
            }
            try {
                UUID world = UUID.fromString(parts[0]);
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                int z = Integer.parseInt(parts[3]);
                return new BlockPosition(world, x, y, z);
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }

        Location toLocation(RollAndDeathSMP plugin) {
            var world = plugin.getServer().getWorld(worldId);
            if (world == null) {
                return null;
            }
            return new Location(world, x, y, z);
        }
    }

    private void loadPersistedTorches() {
        plugin.getDataFolder().mkdirs();
        if (!dataFile.exists()) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        List<String> stored = config.getStringList("torches");
        if (stored.isEmpty()) {
            return;
        }

        for (String entry : stored) {
            BlockPosition position = BlockPosition.deserialize(entry);
            if (position == null) {
                continue;
            }
            Location location = position.toLocation(plugin);
            if (location == null) {
                continue;
            }
            Material type = location.getBlock().getType();
            if (type == Material.TORCH || type == Material.WALL_TORCH || type == Material.SOUL_TORCH || type == Material.SOUL_WALL_TORCH) {
                placedTorches.add(position);
            }
        }
    }

    private void savePersistedTorches() {
        List<String> serialized = new ArrayList<>(placedTorches.size());
        for (BlockPosition position : placedTorches) {
            serialized.add(position.serialize());
        }

        YamlConfiguration config = new YamlConfiguration();
        config.set("torches", serialized);
        try {
            config.save(dataFile);
        } catch (IOException ex) {
            plugin.getLogger().warning("No se pudo guardar eternal_torches.yml: " + ex.getMessage());
        }
    }
}
