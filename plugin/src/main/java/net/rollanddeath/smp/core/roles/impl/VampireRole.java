package net.rollanddeath.smp.core.roles.impl;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class VampireRole extends Role {

    public VampireRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.VAMPIRE);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        handleVampireEffects(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Every second
    }

    private void handleVampireEffects(Player player) {
        long time = player.getWorld().getTime();
        boolean isDay = time > 0 && time < 12300;
        boolean isRaining = player.getWorld().hasStorm();

        if (isDay && !isRaining && player.getLocation().getBlock().getLightFromSky() >= 12) {
            // Burn logic
            ItemStack helmet = player.getInventory().getHelmet();
            if (helmet != null && helmet.getType() != Material.AIR) {
                // Helmet protects but takes damage
                // Logic to damage helmet could go here
                Block block = player.getLocation().getBlock();
                block.getWorld().spawnParticle(org.bukkit.Particle.SMOKE, player.getLocation().add(0, 1, 0), 5, 0.2, 0.2, 0.2, 0.01);
                ItemStack damagedHelmet = helmet.clone();
                damagedHelmet.damage(3, player);
                player.getInventory().setHelmet(damagedHelmet);
            } else {
                player.setFireTicks(60);
            }
        } else {
            // Night or indoors -> Buffs
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 40, 0, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 240, 0, false, false));
        }
    }
}
