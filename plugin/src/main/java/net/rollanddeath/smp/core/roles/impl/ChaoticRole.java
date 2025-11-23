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

import java.util.Random;

public class ChaoticRole extends Role {

    private final Random random = new Random();
    private final PotionEffectType[] effects = {
            PotionEffectType.POISON, PotionEffectType.SLOWNESS, PotionEffectType.WEAKNESS,
            PotionEffectType.BLINDNESS, PotionEffectType.NAUSEA, PotionEffectType.WITHER,
            PotionEffectType.LEVITATION
    };

    public ChaoticRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.CHAOTIC);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && hasRole(player)) {
            if (event.getEntity() instanceof LivingEntity target) {
                if (random.nextDouble() < 0.3) { // 30% chance
                    PotionEffectType type = effects[random.nextInt(effects.length)];
                    target.addPotionEffect(new PotionEffect(type, 100, 0));
                }
            }
        }
    }
}
