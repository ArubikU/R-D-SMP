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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class StarterKitListener implements Listener {

    private final RollAndDeathSMP plugin;
    private final NamespacedKey key;
    private final boolean enabled;
    private final List<ItemStack> kitItems;
    private final boolean includeDailyRolls;
    private final int dailyRollCount;

    public StarterKitListener(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "starter_kit_given");
        this.enabled = plugin.getConfig().getBoolean("starter_kit.enabled", true);
        this.includeDailyRolls = plugin.getConfig().getBoolean("starter_kit.include_daily_rolls", true);
        this.dailyRollCount = Math.max(0, plugin.getConfig().getInt("starter_kit.daily_rolls", 2));
        this.kitItems = loadKitFromConfig();
    }

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!enabled) return;
        if (player.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
            return;
        }

        if (plugin.getConfig().contains("spawn_location")) {
            player.teleport(plugin.getConfig().getLocation("spawn_location"));
        }

        giveKit(player);
        player.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        plugin.getConfig().set("starter_kit.players." + player.getUniqueId(), true);
        plugin.saveConfig();
        player.sendMessage(Component.text("Kit inicial entregado.", NamedTextColor.GREEN));
    }

    private void giveKit(Player player) {
        if (!kitItems.isEmpty()) {
            player.getInventory().addItem(kitItems.toArray(new ItemStack[0]));
        } else {
            // Fallback: old hardcoded kit
            player.getInventory().addItem(buildArmor(Material.IRON_HELMET), buildArmor(Material.IRON_CHESTPLATE), buildArmor(Material.IRON_LEGGINGS), buildArmor(Material.IRON_BOOTS));
            player.getInventory().addItem(buildSword());
            player.getInventory().addItem(new ItemStack(Material.BREAD, 64));
        }

        if (includeDailyRolls && dailyRollCount > 0) {
            var daily = plugin.getDailyRollManager();
            for (int i = 0; i < dailyRollCount; i++) {
                player.getInventory().addItem(daily.rollRewardDirect().clone());
            }
        }
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

    private List<ItemStack> loadKitFromConfig() {
        List<ItemStack> items = new ArrayList<>();
        List<Map<?, ?>> raw = plugin.getConfig().getMapList("starter_kit.items");
        for (Map<?, ?> entry : raw) {
            Object matObj = entry.get("material");
            if (matObj == null) continue;
            Material mat = Material.matchMaterial(matObj.toString());
            if (mat == null) continue;

            int amount = 1;
            Object amountObj = entry.get("amount");
            if (amountObj instanceof Number n) amount = Math.max(1, n.intValue());
            ItemStack stack = new ItemStack(mat, amount);

            ItemMeta meta = stack.getItemMeta();
            if (meta != null) {
                Object nameObj = entry.get("name");
                if (nameObj != null) meta.displayName(Component.text(nameObj.toString()));

                Object unbreakableObj = entry.get("unbreakable");
                if (unbreakableObj instanceof Boolean b && b) meta.setUnbreakable(true);

                Object cmdObj = entry.get("custom_model_data");
                if (cmdObj instanceof Number n) meta.setCustomModelData(n.intValue());

                Object enchObj = entry.get("enchants");
                if (enchObj instanceof Map<?, ?> enchants) {
                    for (Map.Entry<?, ?> e : enchants.entrySet()) {
                        if (!(e.getKey() instanceof String key)) continue;
                        Enchantment ench = Enchantment.getByName(key.toUpperCase(Locale.ROOT));
                        if (ench == null) continue;
                        int level = 1;
                        Object lvlObj = e.getValue();
                        if (lvlObj instanceof Number nLvl) level = Math.max(1, nLvl.intValue());
                        meta.addEnchant(ench, level, true);
                    }
                }

                Object loreObj = entry.get("lore");
                if (loreObj instanceof List<?> loreList) {
                    List<Component> lore = new ArrayList<>();
                    for (Object line : loreList) {
                        lore.add(Component.text(String.valueOf(line)));
                    }
                    meta.lore(lore);
                }

                stack.setItemMeta(meta);
            }

            items.add(stack);
        }
        return items;
    }
}
