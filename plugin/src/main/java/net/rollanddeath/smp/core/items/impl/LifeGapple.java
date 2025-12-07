package net.rollanddeath.smp.core.items.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LifeGapple extends CustomItem {

    public LifeGapple(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.LIFE_GAPPLE);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Consume para ganar +1 vida en el sistema de vidas.", "Se aplica además el efecto de la manzana encantada.");
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (!isItem(item)) return;

        plugin.getLifeManager().addLife(event.getPlayer());
        event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<gold>¡Has ganado +1 vida adicional!"));
    }
}
