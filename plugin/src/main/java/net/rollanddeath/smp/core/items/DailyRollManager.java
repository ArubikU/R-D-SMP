package net.rollanddeath.smp.core.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DailyRollManager {

    private static final long COOLDOWN_MILLIS = 24L * 60 * 60 * 1000;

    private final RollAndDeathSMP plugin;
    private final ItemManager itemManager;
    private final NamespacedKey lastRollKey;
    private final Random random = new Random();

    private final List<ItemStack> commonItems = new ArrayList<>();
    private final List<ItemStack> rareItems = new ArrayList<>();
    private final List<ItemStack> epicItems = new ArrayList<>();
    private final List<ItemStack> legendaryItems = new ArrayList<>();

    public DailyRollManager(RollAndDeathSMP plugin, ItemManager itemManager) {
        this.plugin = plugin;
        this.itemManager = itemManager;
        this.lastRollKey = new NamespacedKey(plugin, "last_daily_roll");
        loadLootTable();
    }

    private void loadLootTable() {
        // Common
        addCustom(commonItems, "HEALING_BANDAGE");
        addCustom(commonItems, "SHARPENING_STONE");
        addCustom(commonItems, "MOLDY_BREAD");
        addCustom(commonItems, "ETERNAL_TORCH");
        addCustom(commonItems, "SHARP_STICK");
        addCustom(commonItems, "REINFORCED_LEATHER_BOOTS");
        addCustom(commonItems, "MYSTERIOUS_SOUP");
        addVanilla(commonItems, Material.BREAD, 5);
        addVanilla(commonItems, Material.IRON_INGOT, 3);
        addVanilla(commonItems, Material.COAL, 8);
        addVanilla(commonItems, Material.OAK_LOG, 16);
        addVanilla(commonItems, Material.COOKED_BEEF, 5);
        addVanilla(commonItems, Material.ARROW, 10);

        // Rare
        addCustom(rareItems, "HERMES_BOOTS");
        addCustom(rareItems, "OBLIVION_POTION");
        addCustom(rareItems, "THORN_SHIELD");
        addCustom(rareItems, "BONE_BOW");
        addCustom(rareItems, "GLASS_PICKAXE");
        addCustom(rareItems, "XP_MAGNET");
        addCustom(rareItems, "SMALL_BACKPACK");
        addCustom(rareItems, "GRAPPLING_HOOK");
        addVanilla(rareItems, Material.DIAMOND, 1);
        addVanilla(rareItems, Material.EMERALD, 3);
        addVanilla(rareItems, Material.GOLDEN_APPLE, 1);
        addVanilla(rareItems, Material.ENDER_PEARL, 2);
        addVanilla(rareItems, Material.BLAZE_ROD, 1);
        addVanilla(rareItems, Material.GHAST_TEAR, 1);

        // Epic
        addCustom(epicItems, "GREED_PICKAXE");
        addCustom(epicItems, "DISCORD_APPLE");
        addCustom(epicItems, "POSEIDON_TRIDENT");
        addCustom(epicItems, "ARMORED_WINGS");
        addCustom(epicItems, "INVISIBILITY_CLOAK");
        addCustom(epicItems, "WAR_HAMMER");
        addCustom(epicItems, "REGENERATION_TOTEM");
        addVanilla(epicItems, Material.NETHERITE_SCRAP, 1);
        addVanilla(epicItems, Material.SHULKER_SHELL, 1);
        addVanilla(epicItems, Material.TOTEM_OF_UNDYING, 1);

        // Legendary
        addCustom(legendaryItems, "CHANCE_TOTEM");
        addCustom(legendaryItems, "RESURRECTION_ORB");
        addCustom(legendaryItems, "VAMPIRE_SWORD");
        addCustom(legendaryItems, "WORLD_DESTROYER_PICKAXE");
        addCustom(legendaryItems, "TRUE_SIGHT_HELMET");
        addCustom(legendaryItems, "NOTCH_HEART");
        addCustom(legendaryItems, "SOUL_CONTRACT");
        addCustom(legendaryItems, "VOID_CALL");
        addCustom(legendaryItems, "REAL_DRAGON_EGG");
        addVanilla(legendaryItems, Material.NETHERITE_INGOT, 1);
        addVanilla(legendaryItems, Material.ENCHANTED_GOLDEN_APPLE, 1);
        addVanilla(legendaryItems, Material.BEACON, 1);
    }

    private void addCustom(List<ItemStack> list, String id) {
        CustomItem item = itemManager.getItem(id);
        if (item == null) {
            plugin.getLogger().warning("DailyRollManager: CustomItem no registrado: " + id);
            return;
        }
        try {
            ItemStack stack = item.getItemStack();
            if (stack != null) {
                list.add(stack);
            } else {
                plugin.getLogger().warning("DailyRollManager: ItemStack null para: " + id);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("DailyRollManager: error construyendo item " + id + ": " + e.getMessage());
        }
    }

    private void addVanilla(List<ItemStack> list, Material material, int amount) {
        list.add(new ItemStack(material, amount));
    }

    public void performRoll(Player player) {
        long now = System.currentTimeMillis();
        long lastRoll = getLastRollTimestamp(player.getUniqueId());

        if (lastRoll > 0) {
            long diff = now - lastRoll;
            if (diff < COOLDOWN_MILLIS) {
                Duration remaining = Duration.ofMillis(COOLDOWN_MILLIS - diff);
                long hours = remaining.toHours();
                long minutes = remaining.minusHours(hours).toMinutes();
                player.sendMessage(Component.text("Debes esperar " + hours + "h " + minutes + "m para tu próximo roll diario.", NamedTextColor.RED));
                return;
            }
        }

        // Perform Roll
        ItemStack reward = rollItem();
        player.getInventory().addItem(reward);
        
        // Save time
        recordRoll(player, now);

        // Effects
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
        player.sendMessage(Component.text("¡Has obtenido: ", NamedTextColor.GREEN)
                .append(reward.displayName().color(getRarityColor(reward))));
        
        if (isLegendary(reward)) {
            plugin.getServer().broadcast(Component.text("¡" + player.getName() + " ha obtenido un objeto LEGENDARIO en su roll diario!", NamedTextColor.GOLD));
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
        }
    }

    private ItemStack rollItem() {
        return rollFromNumber(random.nextDouble() * 100);
    }

    /**
     * Devuelve un ítem aleatorio sin aplicar cooldown ni efectos colaterales.
     */
    public ItemStack rollRewardDirect() {
        return rollItem();
    }

    /**
     * Aplica un modificador de suerte (+0.2 = 20% mejor, -0.2 = 20% peor).
     */
    public ItemStack rollItemWithLuck(double luckModifier) {
        double factor = 1.0 + luckModifier;
        factor = Math.max(0.1, Math.min(2.0, factor));
        double adjustedRoll = random.nextDouble() * 100 * factor;
        return rollFromNumber(adjustedRoll);
    }

    private NamedTextColor getRarityColor(ItemStack item) {
        if (containsSimilar(commonItems, item)) return NamedTextColor.GRAY;
        if (containsSimilar(rareItems, item)) return NamedTextColor.BLUE;
        if (containsSimilar(epicItems, item)) return NamedTextColor.DARK_PURPLE;
        if (containsSimilar(legendaryItems, item)) return NamedTextColor.GOLD;
        return NamedTextColor.WHITE;
    }

    public boolean isRollAvailable(UUID uuid) {
        long last = getLastRollTimestamp(uuid);
        if (last <= 0) {
            return true;
        }
        return System.currentTimeMillis() - last >= COOLDOWN_MILLIS;
    }

    public Duration getTimeUntilNextRoll(UUID uuid) {
        long last = getLastRollTimestamp(uuid);
        if (last <= 0) {
            return Duration.ZERO;
        }
        long elapsed = System.currentTimeMillis() - last;
        if (elapsed >= COOLDOWN_MILLIS) {
            return Duration.ZERO;
        }
        return Duration.ofMillis(COOLDOWN_MILLIS - elapsed);
    }

    private long getLastRollTimestamp(UUID uuid) {
        Player online = plugin.getServer().getPlayer(uuid);
        if (online != null) {
            Long stored = online.getPersistentDataContainer().get(lastRollKey, PersistentDataType.LONG);
            if (stored != null && stored > 0) {
                return stored;
            }
        }

        return plugin.getConfig().getLong("daily.last_roll." + uuid, -1L);
    }

    private void recordRoll(Player player, long timestamp) {
        player.getPersistentDataContainer().set(lastRollKey, PersistentDataType.LONG, timestamp);
        plugin.getConfig().set("daily.last_roll." + player.getUniqueId(), timestamp);
        plugin.saveConfig();
    }

    private boolean containsSimilar(List<ItemStack> list, ItemStack item) {
        for (ItemStack candidate : list) {
            if (candidate.isSimilar(item)) {
                return true;
            }
        }
        return false;
    }

    private boolean isLegendary(ItemStack item) {
        return containsSimilar(legendaryItems, item);
    }

    private ItemStack rollFromNumber(double roll) {
        if (roll < 65) { // 0-64 (65%)
            return commonItems.get(random.nextInt(commonItems.size())).clone();
        } else if (roll < 85) { // 65-84 (20%)
            return rareItems.get(random.nextInt(rareItems.size())).clone();
        } else if (roll < 97) { // 85-96 (12%)
            return epicItems.get(random.nextInt(epicItems.size())).clone();
        } else { // 97-99 (3%)
            return legendaryItems.get(random.nextInt(legendaryItems.size())).clone();
        }
    }
}
