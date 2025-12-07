package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class ChaoticRole extends Role {

    private final Random random = new Random();
    private final PotionEffectType[] negativeEffects = {
        PotionEffectType.POISON, PotionEffectType.SLOWNESS, PotionEffectType.WEAKNESS,
        PotionEffectType.BLINDNESS, PotionEffectType.NAUSEA, PotionEffectType.WITHER,
        PotionEffectType.LEVITATION
    };

    private final PotionEffectType[] positiveEffects = {
        PotionEffectType.SPEED, PotionEffectType.STRENGTH, PotionEffectType.JUMP_BOOST,
        PotionEffectType.REGENERATION, PotionEffectType.FAST_DIGGING, PotionEffectType.DAMAGE_RESISTANCE,
        PotionEffectType.ABSORPTION
    };

    public ChaoticRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.CHAOTIC);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        PotionEffectType type = positiveEffects[random.nextInt(positiveEffects.length)];
                        player.addPotionEffect(new PotionEffect(type, 400, 0));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 6000L); // cada 5 minutos un buff aleatorio
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && hasRole(player)) {
            if (event.getEntity() instanceof LivingEntity target) {
                double roll = random.nextDouble();
                if (roll < 0.2) {
                    PotionEffectType type = positiveEffects[random.nextInt(positiveEffects.length)];
                    player.addPotionEffect(new PotionEffect(type, 100, 0));
                } else if (roll < 0.5) {
                    PotionEffectType type = negativeEffects[random.nextInt(negativeEffects.length)];
                    target.addPotionEffect(new PotionEffect(type, 100, 0));
                }
            }
        }
    }
}
