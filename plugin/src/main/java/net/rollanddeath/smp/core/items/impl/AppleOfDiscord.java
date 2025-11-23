package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class AppleOfDiscord extends CustomItem {

    public AppleOfDiscord(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.DISCORD_APPLE);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.GOLDEN_APPLE);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Fuerza IV (1 min), luego Veneno II (1 min).");
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (!isItem(item)) return;

        Player player = event.getPlayer();
        
        // Strength IV for 1 minute
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20 * 60, 3));
        
        // Schedule Poison II after 1 minute
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 60, 1));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>¡La discordia te envenena!"));
            }
        }, 20 * 60);
    }
}
