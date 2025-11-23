package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class OblivionPotion extends CustomItem {

    public OblivionPotion(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.OBLIVION_POTION);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.POTION);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Borra tu memoria (y tu XP)", "Te deja ciego y débil por un tiempo");
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = super.getItemStack();
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta != null) {
            meta.setColor(Color.BLACK);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 600, 0), true);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 600, 1), true);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (isItem(event.getItem())) {
            Player player = event.getPlayer();
            player.setLevel(0);
            player.setExp(0);
        }
    }
}
