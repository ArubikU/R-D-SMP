package net.rollanddeath.smp.core.mobs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class BossDeathListener implements Listener {

    private final RollAndDeathSMP plugin;
    // Map<BossUUID, Map<PlayerUUID, Damage>>
    private final Map<UUID, Map<UUID, Double>> damageTracker = new HashMap<>();

    public BossDeathListener(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity victim = (LivingEntity) event.getEntity();

        if (!victim.getScoreboardTags().contains("custom_mob")) return;

        // Check if it's a boss
        MobType type = getMobType(victim);
        if (type == null || !type.isBoss()) return;

        Player attacker = null;
        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile proj = (Projectile) event.getDamager();
            if (proj.getShooter() instanceof Player) {
                attacker = (Player) proj.getShooter();
            }
        }

        if (attacker != null) {
            damageTracker.computeIfAbsent(victim.getUniqueId(), k -> new HashMap<>())
                    .merge(attacker.getUniqueId(), event.getFinalDamage(), Double::sum);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        if (!victim.getScoreboardTags().contains("custom_mob")) return;

        MobType type = getMobType(victim);
        if (type == null || !type.isBoss()) return;

        handleBossDeath(victim, type);
    }

    private void handleBossDeath(LivingEntity boss, MobType type) {
        UUID bossId = boss.getUniqueId();
        Map<UUID, Double> damageMap = damageTracker.remove(bossId);

        if (damageMap == null || damageMap.isEmpty()) return;

        // Sort by damage descending
        var topDamagers = damageMap.entrySet().stream()
                .sorted(Map.Entry.<UUID, Double>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());

        // Broadcast
        Component message = Component.text("¡" + type.getDisplayName() + " ha sido derrotado!", NamedTextColor.GOLD)
                .append(Component.newline())
                .append(Component.text("Top Daño:", NamedTextColor.YELLOW));

        int rank = 1;
        for (Map.Entry<UUID, Double> entry : topDamagers) {
            Player p = Bukkit.getPlayer(entry.getKey());
            String name = (p != null) ? p.getName() : "Desconocido";
            
            message = message.append(Component.newline())
                    .append(Component.text("#" + rank + " " + name + " - " + String.format("%.1f", entry.getValue()) + " daño", NamedTextColor.WHITE));
            
            // Award Kill Point
            plugin.getKillPointsManager().addKill(entry.getKey());
            if (p != null) {
                p.sendMessage(Component.text("¡Has recibido 1 Kill Point por tu contribución!", NamedTextColor.GREEN));
            }
            
            rank++;
        }

        Bukkit.broadcast(message);
    }

    private MobType getMobType(Entity entity) {
        for (String tag : entity.getScoreboardTags()) {
            try {
                return MobType.valueOf(tag);
            } catch (IllegalArgumentException ignored) {}
        }
        return null;
    }
}
