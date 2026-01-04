package net.rollanddeath.smp.core.monetization;

import net.kyori.adventure.text.Component;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Particle;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MonetizationManager {

    private final RollAndDeathSMP plugin;
    private final File dataFile;
    private final YamlConfiguration data;
    private final File backpackFile;
    private final YamlConfiguration backpackData;
    private final Map<UUID, Particle> activeTrails = new HashMap<>();

    public MonetizationManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "monetization_enders.yml");
        this.backpackFile = new File(plugin.getDataFolder(), "monetization_backpacks.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException ignored) {
            }
        }
        if (!backpackFile.exists()) {
            try {
                backpackFile.getParentFile().mkdirs();
                backpackFile.createNewFile();
            } catch (IOException ignored) {
            }
        }
        this.data = YamlConfiguration.loadConfiguration(dataFile);
        this.backpackData = YamlConfiguration.loadConfiguration(backpackFile);
    }

    public void openEnderChest(Player player) {
        int rows = resolveEnderRows(player);
        int size = rows * 9;
        Inventory inv = Bukkit.createInventory(new MonetizedEnderHolder(player.getUniqueId(), rows), size,
                Component.text("Ender Chest (" + rows + " filas)"));
        ItemStack[] contents = loadEnderContents(player.getUniqueId(), size);
        inv.setContents(contents);
        player.openInventory(inv);
    }

    public void saveIfEnderChest(Inventory inventory) {
        if (!(inventory.getHolder() instanceof MonetizedEnderHolder holder)) {
            return;
        }

        List<ItemStack> items = Arrays.asList(inventory.getContents());
        List<ItemStack> existing = (List<ItemStack>) data.getList("enders." + holder.getOwner());
        // Preserva overflow si el jugador bajó de tier
        if (existing != null && existing.size() > items.size()) {
            items = new java.util.ArrayList<>(items);
            items.addAll(existing.subList(items.size(), existing.size()));
        }

        data.set("enders." + holder.getOwner().toString(), items);
        try {
            data.save(dataFile);
        } catch (IOException ignored) {
        }
    }

    public boolean canUseVirtualEnder(Player player) {
        return player.hasPermission("rd.enderchest.virtual");
    }

    public boolean canUseCraft(Player player) {
        return player.hasPermission("rd.craft");
    }

    public boolean canUseAnvil(Player player) {
        return player.hasPermission("rd.anvil");
    }

    public boolean canUseEnchant(Player player) {
        return player.hasPermission("rd.enchanting");
    }

    public void openCraft(Player player) {
        player.openWorkbench(null, true);
    }

    public void openAnvil(Player player) {
        Inventory anvil = Bukkit.createInventory(player, InventoryType.ANVIL, Component.text("Yunque portátil"));
        player.openInventory(anvil);
    }

    public void openEnchant(Player player) {
        Inventory enchant = Bukkit.createInventory(player, InventoryType.ENCHANTING, Component.text("Mesa de encantamientos"));
        player.openInventory(enchant);
    }

    public boolean canUseStonecutter(Player player) { return player.hasPermission("rd.stonecutter"); }
    public boolean canUseSmith(Player player) { return player.hasPermission("rd.smith"); }
    public boolean canUseLoom(Player player) { return player.hasPermission("rd.loom"); }
    public boolean canUseGrindstone(Player player) { return player.hasPermission("rd.grindstone"); }
    public boolean canUseCartography(Player player) { return player.hasPermission("rd.cartography"); }

    public void openStonecutter(Player player) {
        Inventory inv = Bukkit.createInventory(player, InventoryType.STONECUTTER, Component.text("Cortapiedra portátil"));
        player.openInventory(inv);
    }

    public void openSmith(Player player) {
        Inventory inv = Bukkit.createInventory(player, InventoryType.SMITHING, Component.text("Meseta de herrería"));
        player.openInventory(inv);
    }

    public void openLoom(Player player) {
        Inventory inv = Bukkit.createInventory(player, InventoryType.LOOM, Component.text("Telar portátil"));
        player.openInventory(inv);
    }

    public void openGrindstone(Player player) {
        Inventory inv = Bukkit.createInventory(player, InventoryType.GRINDSTONE, Component.text("Afiladora portátil"));
        player.openInventory(inv);
    }

    public void openCartography(Player player) {
        Inventory inv = Bukkit.createInventory(player, InventoryType.CARTOGRAPHY, Component.text("Mesa de cartografía"));
        player.openInventory(inv);
    }

    public boolean canUseBackpack(Player player) { return player.hasPermission("rd.backpack"); }
    public int backpackRows(Player player) {
        if (player.hasPermission("rd.backpack.5")) return 5;
        if (player.hasPermission("rd.backpack.4")) return 4;
        if (player.hasPermission("rd.backpack.3")) return 3;
        if (player.hasPermission("rd.backpack.2")) return 2;
        if (player.hasPermission("rd.backpack.1")) return 1;
        return 0;
    }

    public void openBackpack(Player player) {
        int rows = backpackRows(player);
        if (rows <= 0) return;
        int size = rows * 9;
        Inventory inv = Bukkit.createInventory(new BackpackHolder(player.getUniqueId(), rows), size, Component.text("Mochila"));
        inv.setContents(loadBackpack(player.getUniqueId(), size));
        player.openInventory(inv);
    }

    public void saveIfBackpack(Inventory inventory) {
        if (!(inventory.getHolder() instanceof BackpackHolder holder)) return;
        List<ItemStack> items = Arrays.asList(inventory.getContents());
        List<ItemStack> existing = (List<ItemStack>) backpackData.getList("backpacks." + holder.getOwner());
        if (existing != null && existing.size() > items.size()) {
            items = new java.util.ArrayList<>(items);
            items.addAll(existing.subList(items.size(), existing.size()));
        }
        backpackData.set("backpacks." + holder.getOwner(), items);
        try { backpackData.save(backpackFile); } catch (IOException ignored) {}
    }

    public boolean canUseTrash(Player player) { return player.hasPermission("rd.trash" ); }
    public void openTrash(Player player) {
        Inventory inv = Bukkit.createInventory(player, 27, Component.text("Basurero"));
        player.openInventory(inv);
    }

    public boolean canUseFurnace(Player player) { return player.hasPermission("rd.furnace"); }
    public boolean canUseBlast(Player player) { return player.hasPermission("rd.blast"); }
    public boolean canUseSmoker(Player player) { return player.hasPermission("rd.smoker"); }

    public void openFurnace(Player player) {
        Inventory inv = Bukkit.createInventory(player, InventoryType.FURNACE, Component.text("Horno portátil"));
        player.openInventory(inv);
    }

    public void openBlast(Player player) {
        Inventory inv = Bukkit.createInventory(player, InventoryType.BLAST_FURNACE, Component.text("Alto horno portátil"));
        player.openInventory(inv);
    }

    public void openSmoker(Player player) {
        Inventory inv = Bukkit.createInventory(player, InventoryType.SMOKER, Component.text("Ahumador portátil"));
        player.openInventory(inv);
    }

    // Cosmetics: particle trails
    public boolean canUseTrail(Player player, Particle particle) {
        String key = particle.name().toLowerCase();
        return player.hasPermission("rd.trail." + key) || player.hasPermission("rd.trail.*");
    }

    public void setTrail(Player player, Particle particle) {
        if (particle == null) {
            activeTrails.remove(player.getUniqueId());
            return;
        }
        activeTrails.put(player.getUniqueId(), particle);
    }

    public Particle getTrail(UUID playerId) {
        return activeTrails.get(playerId);
    }

    public void spawnTrailIfAny(Player player, Location from, Location to) {
        if (from.getWorld() != to.getWorld()) return;
        if (from.distanceSquared(to) < 0.04) return; // tiny move, skip
        Particle particle = activeTrails.get(player.getUniqueId());
        if (particle == null) return;
        player.getWorld().spawnParticle(particle, to.clone().add(0, 0.1, 0), 4, 0.15, 0.05, 0.15, 0.0);
    }

    private ItemStack[] loadEnderContents(UUID owner, int size) {
        List<ItemStack> stored = (List<ItemStack>) data.getList("enders." + owner.toString());
        ItemStack[] contents = new ItemStack[size];

        if (stored == null || stored.isEmpty()) {
            Player p = Bukkit.getPlayer(owner);
            if (p != null) {
                ItemStack[] vanilla = p.getEnderChest().getContents();
                for (int i = 0; i < Math.min(size, vanilla.length); i++) contents[i] = vanilla[i];
            }
        } else {
            for (int i = 0; i < Math.min(size, stored.size()); i++) contents[i] = stored.get(i);
        }

        return contents;
    }

    private ItemStack[] loadBackpack(UUID owner, int size) {
        List<ItemStack> stored = (List<ItemStack>) backpackData.getList("backpacks." + owner);
        ItemStack[] contents = new ItemStack[size];
        if (stored != null) {
            for (int i = 0; i < Math.min(size, stored.size()); i++) contents[i] = stored.get(i);
        }
        return contents;
    }

    private int resolveEnderRows(Player player) {
        // Highest tier wins
        if (player.hasPermission("rd.enderchest.6")) return 6;
        if (player.hasPermission("rd.enderchest.5")) return 5;
        if (player.hasPermission("rd.enderchest.4")) return 4;
        return 3; // Vanilla size
    }

    /**
     * Marker holder to identify custom ender chest inventories.
     */
    public static class MonetizedEnderHolder implements InventoryHolder {
        private final UUID owner;
        private final int rows;

        public MonetizedEnderHolder(UUID owner, int rows) {
            this.owner = owner;
            this.rows = rows;
        }

        public UUID getOwner() {
            return owner;
        }

        public int getRows() {
            return rows;
        }

        @Override
        public Inventory getInventory() {
            return Bukkit.createInventory(this, rows * 9, Component.text("Ender Chest"));
        }
    }

    public static class BackpackHolder implements InventoryHolder {
        private final UUID owner;
        private final int rows;

        public BackpackHolder(UUID owner, int rows) {
            this.owner = owner;
            this.rows = rows;
        }

        public UUID getOwner() { return owner; }
        public int getRows() { return rows; }

        @Override
        public Inventory getInventory() {
            return Bukkit.createInventory(this, rows * 9, Component.text("Mochila"));
        }
    }
}
