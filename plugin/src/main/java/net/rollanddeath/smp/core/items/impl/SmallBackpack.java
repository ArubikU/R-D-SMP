package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;

public class SmallBackpack extends CustomItem {

    private final NamespacedKey contentKey;

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
        
        Inventory inv = Bukkit.createInventory(player, 9, "Mochila Pequeña");
        
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
        if (!event.getView().getTitle().equals("Mochila Pequeña")) return;
        
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

    private String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(items.length);
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    private ItemStack[] itemStackArrayFromBase64(String data) throws java.io.IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }
            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new java.io.IOException("Unable to decode class type.", e);
        }
    }
}
