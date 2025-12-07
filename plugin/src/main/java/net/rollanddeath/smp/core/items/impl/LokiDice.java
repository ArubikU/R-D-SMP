package net.rollanddeath.smp.core.items.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import net.rollanddeath.smp.core.items.DailyRollManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class LokiDice extends CustomItem {

    private final DailyRollManager dailyRollManager;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final org.bukkit.NamespacedKey luckKey;

    public LokiDice(RollAndDeathSMP plugin, DailyRollManager dailyRollManager) {
        super(plugin, CustomItemType.LOKI_DICE);
        this.dailyRollManager = dailyRollManager;
        this.luckKey = new org.bukkit.NamespacedKey(plugin, "loki_dice_luck");
    }

    @Override
    public ItemStack getItemStack() {
        return getItemStack(0.20); // Por defecto, versión afortunada
    }

    public ItemStack getItemStack(double luckModifier) {
        ItemStack item = createBaseItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.displayName(mm.deserialize("<!i><white>" + type.getDisplayName()));
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, type.name());
        meta.getPersistentDataContainer().set(luckKey, PersistentDataType.DOUBLE, luckModifier);

        Integer cmd = type.getCustomModelData();
        if (cmd != null) {
            meta.setCustomModelData(cmd);
        }

        String luckLabel = luckModifier >= 0 ? "+" + (int) (luckModifier * 100) + "%" : (int) (luckModifier * 100) + "%";
        List<Component> lore = new ArrayList<>();
        lore.add(mm.deserialize("<!i><gray>Click derecho: roll diario inmediato"));
        lore.add(mm.deserialize("<!i><gray>No consume tu cooldown"));
        lore.add(mm.deserialize("<!i><gray>Suerte: " + luckLabel));
        meta.lore(lore);

        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.EMERALD);
    }

    @Override
    protected List<String> getLore() {
        // No se usa porque generamos la lore en getItemStack(double)
        return List.of();
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("RIGHT")) return;
        ItemStack item = event.getItem();
        if (!isItem(item)) return;

        event.setCancelled(true);

        double luck = item.getItemMeta()
                .getPersistentDataContainer()
                .getOrDefault(luckKey, PersistentDataType.DOUBLE, 0.0);

        ItemStack reward = dailyRollManager.rollItemWithLuck(luck).clone();
        event.getPlayer().getInventory().addItem(reward);

        item.setAmount(item.getAmount() - 1);

        Component name = reward.displayName() != null ? reward.displayName() : Component.text(reward.getType().name());
        event.getPlayer().sendMessage(mm.deserialize("<green>Lanzaste el Dado de Loki y obtuviste: </green>").append(name));
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, luck >= 0 ? 1.2f : 0.8f);
    }
}