package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
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
        AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
        
        if (attribute != null) {
            NamespacedKey key = new NamespacedKey(plugin, "notch_heart");
            double currentBonus = 0.0;

            // Find existing modifier and remove it to update
            for (AttributeModifier modifier : attribute.getModifiers()) {
                if (key.equals(modifier.getKey())) {
                    currentBonus = modifier.getAmount();
                    attribute.removeModifier(modifier);
                    break;
                }
            }

            double newBonus = currentBonus + 2.0;
            AttributeModifier newModifier = new AttributeModifier(key, newBonus, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY);
            attribute.addModifier(newModifier);
            
            player.sendMessage(MiniMessage.miniMessage().deserialize("<gold>¡Tu vida máxima ha aumentado!"));
        }
    }
}
