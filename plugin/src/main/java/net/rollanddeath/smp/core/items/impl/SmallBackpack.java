package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class SmallBackpack extends CustomItem {

    private final NamespacedKey contentKey;
    private final Component title = Component.text("Mochila Pequeña");

    public SmallBackpack(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.SMALL_BACKPACK);
        this.contentKey = new NamespacedKey(plugin, "backpack_contents");
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.CHEST);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Añade 9 espacios de inventario extra.");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("RIGHT")) return;
        ItemStack item = event.getItem();
        if (!isItem(item)) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        
        Inventory inv = Bukkit.createInventory(player, 9, title);
        
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (container.has(contentKey, PersistentDataType.STRING)) {
            String data = container.get(contentKey, PersistentDataType.STRING);
            try {
                ItemStack[] contents = itemStackArrayFromBase64(data);
                inv.setContents(contents);
            } catch (Exception e) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Error al abrir la mochila."));
                e.printStackTrace();
            }
        }
        
        player.openInventory(inv);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!event.getView().title().equals(title)) return;
        
        Inventory inv = event.getInventory();
        Player player = (Player) event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (!isItem(item)) {
            item = player.getInventory().getItemInOffHand();
            if (!isItem(item)) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>¡Debes sostener la mochila para guardarla!"));
                for (ItemStack content : inv.getContents()) {
                    if (content != null) {
                        player.getWorld().dropItemNaturally(player.getLocation(), content);
                    }
                }
                return;
            }
        }

        try {
            String data = itemStackArrayToBase64(inv.getContents());
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(contentKey, PersistentDataType.STRING, data);
            item.setItemMeta(meta);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Mochila guardada."));
        } catch (Exception e) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Error al guardar la mochila."));
            e.printStackTrace();
        }
    }

    private String itemStackArrayToBase64(ItemStack[] items) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("c", items);
        return config.saveToString();
    }

    private ItemStack[] itemStackArrayFromBase64(String data) throws java.io.IOException {
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.loadFromString(data);
            List<?> list = config.getList("c");
            if (list == null) return new ItemStack[0];
            return list.toArray(new ItemStack[0]);
        } catch (Exception e) {
            throw new java.io.IOException("Unable to decode item stacks.", e);
        }
    }
}
