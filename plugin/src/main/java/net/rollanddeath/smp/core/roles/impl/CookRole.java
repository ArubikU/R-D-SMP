package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CookRole extends Role {

    public CookRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.COOK);
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (hasRole(player)) {
            if (event.getItem().getType().isEdible()) {
                player.setSaturation(player.getSaturation() + 5);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 0));
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 600, 3));
            }
            if (isRawMeat(event.getItem().getType())) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
            }
        }
    }

    private boolean isRawMeat(Material type) {
        switch (type) {
            case BEEF:
            case PORKCHOP:
            case CHICKEN:
            case RABBIT:
            case MUTTON:
            case COD:
            case SALMON:
            case TROPICAL_FISH:
            case PUFFERFISH:
            case ROTTEN_FLESH:
                return true;
            default:
                return false;
        }
    }
}
