package net.rollanddeath.smp.core.items;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemManager {

    private final RollAndDeathSMP plugin;
    private final Map<CustomItemType, CustomItem> items = new HashMap<>();

    public ItemManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    public void registerItem(CustomItem item) {
        items.put(item.getType(), item);
        plugin.getServer().getPluginManager().registerEvents(item, plugin);
    }

    public CustomItem getItem(CustomItemType type) {
        return items.get(type);
    }

    public void giveItem(Player player, CustomItemType type, int amount) {
        CustomItem item = items.get(type);
        if (item != null) {
            ItemStack stack = item.getItemStack();
            stack.setAmount(amount);
            player.getInventory().addItem(stack);
        }
    }
}
