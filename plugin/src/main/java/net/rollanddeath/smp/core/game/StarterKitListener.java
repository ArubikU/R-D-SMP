package net.rollanddeath.smp.core.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class StarterKitListener implements Listener {

    private final RollAndDeathSMP plugin;
    private final NamespacedKey key;

    public StarterKitListener(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "starter_kit_given");
    }

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
            return;
        }

        if (plugin.getConfig().contains("spawn_location")) {
            player.teleport(plugin.getConfig().getLocation("spawn_location"));
        }

        giveKit(player);
        player.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        plugin.getConfig().set("starter_kit." + player.getUniqueId(), true);
        plugin.saveConfig();
        player.sendMessage(Component.text("Kit inicial entregado.", NamedTextColor.GREEN));
    }

    private void giveKit(Player player) {
        player.getInventory().addItem(buildArmor(Material.IRON_HELMET), buildArmor(Material.IRON_CHESTPLATE), buildArmor(Material.IRON_LEGGINGS), buildArmor(Material.IRON_BOOTS));
        player.getInventory().addItem(buildSword());
        player.getInventory().addItem(new ItemStack(Material.BREAD, 64));

        // Dos ítems diarios aleatorios sin cooldown
        var daily = plugin.getDailyRollManager();
        player.getInventory().addItem(daily.rollRewardDirect().clone());
        player.getInventory().addItem(daily.rollRewardDirect().clone());
    }

    private ItemStack buildArmor(Material type) {
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.PROTECTION, 2, true);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildSword() {
        ItemStack item = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.SHARPNESS, 2, true);
            item.setItemMeta(meta);
        }
        return item;
    }
}
