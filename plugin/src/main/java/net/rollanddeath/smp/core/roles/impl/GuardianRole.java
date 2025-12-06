package net.rollanddeath.smp.core.roles.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GuardianRole extends Role {

    private static final double REDIRECT_RADIUS_SQUARED = 25.0; // 5 blocks^2
    private static final double REDIRECT_RATIO = 0.3; // 30% of ally damage
    private final Set<UUID> redirecting = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public GuardianRole(RollAndDeathSMP plugin) {
        super(plugin, RoleType.GUARDIAN);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    if (hasRole(player)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 0, false, false));
                        for (Entity nearby : player.getNearbyEntities(5, 5, 5)) {
                            if (nearby instanceof Player ally) {
                                ally.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 40, 0, false, false));
                                ally.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 0, false, false));
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAllyDamaged(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player target)) return;
        if (redirecting.contains(target.getUniqueId())) return;

        Player guardian = findNearbyGuardian(target);
        if (guardian == null) return;

        double share = event.getDamage() * REDIRECT_RATIO;
        if (share <= 0) return;

        event.setDamage(Math.max(0, event.getDamage() - share));
        redirectDamage(guardian, share, target);
    }

    private Player findNearbyGuardian(Player target) {
        for (Player candidate : target.getWorld().getPlayers()) {
            if (candidate.equals(target)) continue;
            if (!hasRole(candidate)) continue;
            if (candidate.getLocation().distanceSquared(target.getLocation()) <= REDIRECT_RADIUS_SQUARED) {
                return candidate;
            }
        }
        return null;
    }

    private void redirectDamage(Player guardian, double damage, Player source) {
        redirecting.add(guardian.getUniqueId());
        guardian.damage(damage, source);
        redirecting.remove(guardian.getUniqueId());
    }
}
