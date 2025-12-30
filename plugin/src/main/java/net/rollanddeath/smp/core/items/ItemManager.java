package net.rollanddeath.smp.core.items;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemManager {

    private final RollAndDeathSMP plugin;
    private final Map<String, CustomItem> items = new HashMap<>();

    public ItemManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    public void registerItem(CustomItem item) {
        if (items.containsKey(item.getId())) {
            unregisterItem(item.getId());
        }
        items.put(item.getId(), item);
        plugin.getServer().getPluginManager().registerEvents(item, plugin);
    }

    public void unregisterItem(String id) {
        CustomItem item = items.remove(id);
        if (item != null) {
            org.bukkit.event.HandlerList.unregisterAll(item);
        }
    }

    public boolean isRegistered(String id) {
        return items.containsKey(id);
    }

    public CustomItem getItem(String id) {
        return items.get(id);
    }
    
    public Map<String, CustomItem> getItems() {
        return java.util.Collections.unmodifiableMap(items);
    }
    
    public java.util.Set<String> getItemIds() {
        return items.keySet();
    }

    public void giveItem(Player player, String id, int amount) {
        giveItem(player, id, amount, null);
    }

    public void giveItem(Player player, String id, int amount, Map<String, Object> extraPdc) {
        CustomItem item = items.get(id);
        if (item != null) {
            ItemStack stack = item.getItemStack(extraPdc);
            stack.setAmount(amount);
            player.getInventory().addItem(stack);
        }
    }
    
    public void unregisterAll() {
        // Unregister events? Bukkit doesn't make this easy without HandlerList.
        // For now just clear the map.
        items.clear();
    }
}
