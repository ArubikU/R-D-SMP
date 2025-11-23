package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class TrueSightHelmet extends CustomItem {

    public TrueSightHelmet(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.TRUE_SIGHT_HELMET);
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ItemStack helmet = player.getInventory().getHelmet();
                if (isItem(helmet)) {
                    for (Entity entity : player.getNearbyEntities(20, 20, 20)) {
                        if (entity instanceof LivingEntity && entity != player) {
                            ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 0, false, false, false));
                        }
                    }
                }
            }
        }, 20L, 20L);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.GOLDEN_HELMET);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Ves mobs invisibles y ores a través de paredes.");
    }
}
