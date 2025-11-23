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

public class MysteriousSoup extends CustomItem {

    public MysteriousSoup(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.MYSTERIOUS_SOUP);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.MUSHROOM_STEW);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Nadie sabe qué contiene...", "Efecto aleatorio al consumir");
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (isItem(event.getItem())) {
            Player player = event.getPlayer();
            PotionEffectType[] effects = {
                PotionEffectType.SPEED, PotionEffectType.SLOW, PotionEffectType.FAST_DIGGING, 
                PotionEffectType.SLOW_DIGGING, PotionEffectType.INCREASE_DAMAGE, PotionEffectType.HEAL, 
                PotionEffectType.HARM, PotionEffectType.JUMP, PotionEffectType.CONFUSION, 
                PotionEffectType.REGENERATION, PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.FIRE_RESISTANCE, 
                PotionEffectType.WATER_BREATHING, PotionEffectType.INVISIBILITY, PotionEffectType.BLINDNESS, 
                PotionEffectType.NIGHT_VISION, PotionEffectType.HUNGER, PotionEffectType.WEAKNESS, 
                PotionEffectType.POISON, PotionEffectType.WITHER, PotionEffectType.HEALTH_BOOST, 
                PotionEffectType.ABSORPTION, PotionEffectType.SATURATION, PotionEffectType.GLOWING, 
                PotionEffectType.LEVITATION, PotionEffectType.LUCK, PotionEffectType.UNLUCK, 
                PotionEffectType.SLOW_FALLING, PotionEffectType.CONDUIT_POWER, PotionEffectType.DOLPHINS_GRACE
            };
            
            PotionEffectType randomEffect = effects[new Random().nextInt(effects.length)];
            player.addPotionEffect(new PotionEffect(randomEffect, 400, 1)); // 20 seconds
        }
    }
}
