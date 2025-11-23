package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class NotchHeart extends CustomItem {

    public NotchHeart(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.NOTCH_HEART);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
    }

    @Override
    protected List<String> getLore() {
        return List.of("+1 Vida Extra permanente. Drop único.");
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (!isItem(item)) return;

        Player player = event.getPlayer();
        // Increase Max Health
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth + 2.0);
        player.sendMessage(MiniMessage.miniMessage().deserialize("<gold>¡Tu vida máxima ha aumentado!"));
    }
}
