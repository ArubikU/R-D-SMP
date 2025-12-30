package net.rollanddeath.smp.core.mobs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.scripted.ScriptedMob;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
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
        String mobId = getMobId(victim);
        if (mobId == null) return;

        CustomMob mob = plugin.getMobManager().getMob(mobId);
        if (mob == null) return;

        boolean isBoss = false;
        if (mob instanceof ScriptedMob sm) {
            isBoss = sm.definition().isBoss();
        }

        if (!isBoss) return;

        // Evita dar KP/trackear para sub-spawns (ej: splits del Slime King)
        if (!isEligibleBossEntity(victim, mobId)) return;

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

        String mobId = getMobId(victim);
        if (mobId == null) return;

        CustomMob mob = plugin.getMobManager().getMob(mobId);
        if (mob == null) return;

        boolean isBoss = false;
        if (mob instanceof ScriptedMob sm) {
            isBoss = sm.definition().isBoss();
        }

        if (!isBoss) return;

        // Solo el boss "real" debe dar KP (en Slime King: solo el grande)
        if (!isEligibleBossEntity(victim, mobId)) return;

        handleBossDeath(victim, mob);
    }

    private void handleBossDeath(LivingEntity boss, CustomMob mob) {
        UUID bossId = boss.getUniqueId();
        Map<UUID, Double> damageMap = damageTracker.remove(bossId);

        if (damageMap == null || damageMap.isEmpty()) return;

        // Sort by damage descending
        var topDamagers = damageMap.entrySet().stream()
                .sorted(Map.Entry.<UUID, Double>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());

        // Broadcast
        Component message = Component.text("¡" + mob.getDisplayName() + " ha sido derrotado!", NamedTextColor.GOLD)
                .append(Component.newline())
                .append(Component.text("Top Daño:", NamedTextColor.YELLOW));

        int rank = 1;
        for (Map.Entry<UUID, Double> entry : topDamagers) {
            Player p = Bukkit.getPlayer(entry.getKey());
            String name = (p != null) ? p.getName() : "Desconocido";
            
            message = message.append(Component.newline())
                    .append(Component.text("#" + rank + " " + name + " - " + String.format("%.1f", entry.getValue()) + " daño", NamedTextColor.WHITE));
            
            // Award Kill Point
            try {
                var kpm = plugin.getKillPointsManager();
                if (kpm != null && kpm.isKillPointsEnabled()) {
                    kpm.addKill(entry.getKey());
                    if (p != null) {
                        p.sendMessage(Component.text("¡Has recibido 1 Kill Point por tu contribución!", NamedTextColor.GREEN));
                    }
                }
            } catch (Exception ignored) {
            }
            
            rank++;
        }

        Bukkit.broadcast(message);
    }

    private String getMobId(Entity entity) {
        if (plugin.getMobManager() == null) return null;
        for (String tag : entity.getScoreboardTags()) {
            if (plugin.getMobManager().getMobIds().contains(tag)) {
                return tag;
            }
        }
        return null;
    }

    private boolean isEligibleBossEntity(LivingEntity entity, String mobId) {
        // Slime King: solo el grande otorga Kill Points.
        // Nota: el Slime King se setea a size 10 en mobs.yml.
        if ("SLIME_KING".equals(mobId)) {
            if (entity instanceof Slime s) {
                return s.getSize() >= 10;
            }
            return false;
        }
        return true;
    }
}
