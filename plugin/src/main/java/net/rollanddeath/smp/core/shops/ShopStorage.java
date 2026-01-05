package net.rollanddeath.smp.core.shops;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ShopStorage {

    private final RollAndDeathSMP plugin;
    private final File file;

    public ShopStorage(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "shops.yml");
        plugin.getDataFolder().mkdirs();
    }

    public Map<Location, Shop> load() {
        Map<Location, Shop> map = new HashMap<>();
        if (!file.exists()) return map;

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection shopsSec = cfg.getConfigurationSection("shops");
        if (shopsSec == null) return map;

        for (String key : shopsSec.getKeys(false)) {
            ConfigurationSection sec = shopsSec.getConfigurationSection(key);
            if (sec == null) continue;
            Location signLoc = readLocation(sec.getConfigurationSection("sign"));
            Location containerLoc = readLocation(sec.getConfigurationSection("container"));
            if (signLoc == null || containerLoc == null) continue;
            String ownerStr = sec.getString("owner");
            if (ownerStr == null) continue;
            UUID owner;
            try {
                owner = UUID.fromString(ownerStr);
            } catch (IllegalArgumentException e) {
                continue;
            }

            ItemDescriptor sell = readDescriptor(sec.getConfigurationSection("sell"));
            ItemDescriptor price = readDescriptor(sec.getConfigurationSection("price"));
            int sellAmount = sec.getInt("sellAmount", 1);
            int priceAmount = sec.getInt("priceAmount", 1);
            if (sell == null || price == null) continue;

            Shop shop = new Shop(owner, signLoc, containerLoc, sell, sellAmount, price, priceAmount);
            ConfigurationSection walletSec = sec.getConfigurationSection("wallet");
            if (walletSec != null) {
                for (String wKey : walletSec.getKeys(false)) {
                    ItemStack stack = walletSec.getItemStack(wKey);
                    if (stack != null) {
                        shop.addPayment(stack);
                        continue;
                    }
                    // Fallback: legacy descriptor + amount storage
                    ConfigurationSection legacy = walletSec.getConfigurationSection(wKey + ".item");
                    if (legacy != null) {
                        ItemDescriptor desc = readDescriptor(legacy);
                        int amt = walletSec.getInt(wKey + ".amount", 0);
                        if (desc != null && amt > 0) {
                            int remaining = amt;
                            while (remaining > 0) {
                                int take = Math.min(remaining, 64);
                                ItemStack rebuilt = desc.toItem(take);
                                if (rebuilt != null) {
                                    shop.addPayment(rebuilt);
                                }
                                remaining -= take;
                            }
                        }
                    }
                }
            }
            map.put(signLoc, shop);
        }
        return map;
    }

    public void save(Map<Location, Shop> shops) {
        YamlConfiguration cfg = new YamlConfiguration();
        ConfigurationSection shopsSec = cfg.createSection("shops");
        int idx = 0;
        for (Shop shop : shops.values()) {
            ConfigurationSection sec = shopsSec.createSection(String.valueOf(idx++));
            sec.set("owner", shop.getOwner().toString());
            writeLocation(sec.createSection("sign"), shop.getSignLocation());
            writeLocation(sec.createSection("container"), shop.getContainerLocation());
            writeDescriptor(sec.createSection("sell"), shop.getSellItem());
            writeDescriptor(sec.createSection("price"), shop.getPriceItem());
            sec.set("sellAmount", shop.getSellAmount());
            sec.set("priceAmount", shop.getPriceAmount());

            ConfigurationSection walletSec = sec.createSection("wallet");
            int w = 0;
            for (ItemStack stack : shop.getWallet()) {
                walletSec.set(String.valueOf(w++), stack);
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("No se pudo guardar shops.yml: " + e.getMessage());
        }
    }

    private void writeLocation(ConfigurationSection sec, Location loc) {
        if (sec == null || loc == null) return;
        sec.set("world", loc.getWorld().getName());
        sec.set("x", loc.getBlockX());
        sec.set("y", loc.getBlockY());
        sec.set("z", loc.getBlockZ());
    }

    private Location readLocation(ConfigurationSection sec) {
        if (sec == null) return null;
        String worldName = sec.getString("world");
        World world = worldName == null ? null : Bukkit.getWorld(worldName);
        if (world == null) return null;
        return new Location(world, sec.getInt("x"), sec.getInt("y"), sec.getInt("z"));
    }

    private void writeDescriptor(ConfigurationSection sec, ItemDescriptor desc) {
        if (sec == null || desc == null) return;
        sec.set("material", desc.material());
        sec.set("enchants", desc.enchants());
        if (desc.customId() != null) {
            sec.set("customId", desc.customId());
        }
    }

    private ItemDescriptor readDescriptor(ConfigurationSection sec) {
        if (sec == null) return null;
        String material = sec.getString("material");
        if (material == null) return null;
        Map<String, Integer> ench = new HashMap<>();
        ConfigurationSection enchSec = sec.getConfigurationSection("enchants");
        if (enchSec != null) {
            for (String k : enchSec.getKeys(false)) {
                ench.put(k, enchSec.getInt(k, 1));
            }
        }
        String cid = sec.getString("customId", null);
        return new ItemDescriptor(material, ench, cid);
    }
}
