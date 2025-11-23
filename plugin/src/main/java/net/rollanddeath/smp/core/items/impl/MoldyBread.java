package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class MoldyBread extends CustomItem {

    public MoldyBread(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.MOLDY_BREAD);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.BREAD);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Parece viejo...", "Puede darte hambre o saturación");
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (isItem(event.getItem())) {
            Player player = event.getPlayer();
            if (new Random().nextBoolean()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 1));
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 100, 0));
            }
        }
    }
}
