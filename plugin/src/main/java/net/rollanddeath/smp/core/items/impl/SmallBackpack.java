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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SmallBackpack extends CustomItem {

    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private final NamespacedKey contentKey;
    private final NamespacedKey idKey;
    private final Map<UUID, BackpackSession> openBackpacks = new HashMap<>();
    private final Component title = Component.text("Mochila Pequeña");

    public SmallBackpack(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.SMALL_BACKPACK);
        this.contentKey = new NamespacedKey(plugin, "backpack_contents");
        this.idKey = new NamespacedKey(plugin, "backpack_id");
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
        String backpackId = ensureBackpackId(item);

        Inventory inv = Bukkit.createInventory(player, 9, title);
        
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (container.has(contentKey, PersistentDataType.STRING)) {
            String data = container.get(contentKey, PersistentDataType.STRING);
            try {
                ItemStack[] contents = itemStackArrayFromBase64(data);
                inv.setContents(contents);
            } catch (Exception e) {
                player.sendMessage(MINI.deserialize("<red>Error al abrir la mochila."));
                e.printStackTrace();
            }
        }
        openBackpacks.put(player.getUniqueId(), new BackpackSession(backpackId, inv));
        player.openInventory(inv);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!event.getView().title().equals(title)) return;
        BackpackSession session = openBackpacks.remove(event.getPlayer().getUniqueId());
        if (session == null || !event.getInventory().equals(session.inventory)) {
            return;
        }

        Inventory inv = event.getInventory();
        Player player = (Player) event.getPlayer();
        ItemStack item = findBackpackItem(player, session.backpackId);

        if (item == null) {
            player.sendMessage(MINI.deserialize("<red>¡Debes sostener la mochila para guardarla!"));
            dropInventoryContents(inv, player);
            return;
        }

        try {
            String data = itemStackArrayToBase64(inv.getContents());
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(contentKey, PersistentDataType.STRING, data);
            item.setItemMeta(meta);
            player.sendMessage(MINI.deserialize("<green>Mochila guardada."));
        } catch (Exception e) {
            player.sendMessage(MINI.deserialize("<red>Error al guardar la mochila."));
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        BackpackSession session = openBackpacks.get(player.getUniqueId());
        if (session == null || !event.getView().title().equals(title)) {
            return;
        }

        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        boolean hotbarSwap = false;
        int hotbarButton = event.getHotbarButton();
        if (hotbarButton >= 0 && hotbarButton < player.getInventory().getSize()) {
            ItemStack hotbarItem = player.getInventory().getItem(hotbarButton);
            hotbarSwap = isSameBackpack(hotbarItem, session.backpackId);
        }

        if ((cursor != null && isSameBackpack(cursor, session.backpackId)) ||
            (current != null && isSameBackpack(current, session.backpackId)) ||
            hotbarSwap) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        openBackpacks.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        BackpackSession session = openBackpacks.get(player.getUniqueId());
        if (session == null) {
            return;
        }
        if (isSameBackpack(event.getItemDrop().getItemStack(), session.backpackId)) {
            event.setCancelled(true);
            player.sendMessage(MINI.deserialize("<red>No puedes soltar la mochila mientras está abierta."));
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

    private String ensureBackpackId(ItemStack item) {
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String id = container.get(idKey, PersistentDataType.STRING);
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
            container.set(idKey, PersistentDataType.STRING, id);
            item.setItemMeta(meta);
        }
        return id;
    }

    private ItemStack findBackpackItem(Player player, String id) {
        for (ItemStack stack : player.getInventory().getContents()) {
            if (isSameBackpack(stack, id)) {
                return stack;
            }
        }
        ItemStack offhand = player.getInventory().getItemInOffHand();
        if (isSameBackpack(offhand, id)) {
            return offhand;
        }
        return null;
    }

    private boolean isSameBackpack(ItemStack stack, String id) {
        if (!isItem(stack) || id == null) {
            return false;
        }
        org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return false;
        }
        String stored = meta.getPersistentDataContainer().get(idKey, PersistentDataType.STRING);
        return id.equals(stored);
    }

    private void dropInventoryContents(Inventory inventory, Player player) {
        for (ItemStack content : inventory.getContents()) {
            if (content != null) {
                player.getWorld().dropItemNaturally(player.getLocation(), content);
            }
        }
    }

    private record BackpackSession(String backpackId, Inventory inventory) {}
}
