package net.rollanddeath.smp.core.combat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class CombatLogManager implements Listener {

    private final RollAndDeathSMP plugin;
    private final ReanimationManager reanimationManager;
    private final TeamManager teamManager;
    private final Settings settings;
    private final Map<UUID, CombatTag> combatTags = new HashMap<>();
    private final Map<UUID, BossBar> bossBars = new HashMap<>();
    private BukkitTask bossBarTask;

    public CombatLogManager(RollAndDeathSMP plugin, ReanimationManager reanimationManager, TeamManager teamManager) {
        this.plugin = plugin;
        this.reanimationManager = reanimationManager;
        this.teamManager = teamManager;
        this.settings = new Settings(plugin.getConfig().getConfigurationSection("deathmatch"));
        if (settings.combatLogEnabled) {
            startBossBarTask();
        }
    }

    public boolean isEnabled() {
        return settings.combatLogEnabled;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!settings.combatLogEnabled) {
            return;
        }

        Player victim = event.getEntity() instanceof Player p ? p : null;
        Player attacker = resolveAttacker(event);

        if (victim == null || attacker == null) {
            return;
        }

        var kp = plugin.getKillPointsManager();
        if (kp != null && !kp.isPvpEnabled()) {
            event.setCancelled(true);
            attacker.sendMessage(Component.text("El PvP está desactivado por ahora.", NamedTextColor.RED));
            return;
        }
        if (teamManager != null) {
            var victimTeam = teamManager.getTeam(victim.getUniqueId());
            var attackerTeam = teamManager.getTeam(attacker.getUniqueId());
            if (victimTeam != null && victimTeam.equals(attackerTeam)) {
                return; // same-team hits do not trigger combat log
            }
        }
        if (attacker.equals(victim)) {
            return;
        }
        if (victim.isDead() || attacker.isDead()) {
            return;
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            return;
        }

        long expireAt = System.currentTimeMillis() + (settings.combatCooldownSeconds * 1000L);
        combatTags.put(attacker.getUniqueId(), new CombatTag(victim.getUniqueId(), expireAt));
        combatTags.put(victim.getUniqueId(), new CombatTag(attacker.getUniqueId(), expireAt));

        updateBossBar(attacker.getUniqueId());
        updateBossBar(victim.getUniqueId());

        attacker.sendActionBar(Component.text("En combate con " + victim.getName(), NamedTextColor.RED));
        victim.sendActionBar(Component.text("En combate con " + attacker.getName(), NamedTextColor.RED));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!settings.combatLogEnabled) {
            return;
        }

        Player player = event.getPlayer();
        CombatTag tag = getActiveTag(player.getUniqueId());
        if (tag == null) {
            combatTags.remove(player.getUniqueId());
            clearBossBar(player.getUniqueId());
            return;
        }

        combatTags.remove(player.getUniqueId());
        removeReciprocal(tag);
        clearBossBar(player.getUniqueId());
        clearBossBar(tag.opponent());

        reanimationManager.markForNaturalDeath(player);
        Component message = Component.text(player.getName() + " se desconectó en combate.", NamedTextColor.DARK_RED);
        if (settings.broadcastOnKill) {
            Bukkit.broadcast(message);
        } else {
            Player opponent = Bukkit.getPlayer(tag.opponent());
            if (opponent != null && opponent.isOnline()) {
                opponent.sendMessage(message);
            }
        }
        player.setHealth(0.0D);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        UUID uuid = event.getEntity().getUniqueId();
        CombatTag tag = combatTags.remove(uuid);
        if (tag != null) {
            removeReciprocal(tag);
        }
        clearBossBar(uuid);
    }

    private Player resolveAttacker(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Player player) {
            return player;
        }

        if (damager instanceof Projectile projectile) {
            ProjectileSource source = projectile.getShooter();
            if (source instanceof Player player) {
                return player;
            }
        }
        return null;
    }

    private CombatTag getActiveTag(UUID uuid) {
        CombatTag tag = combatTags.get(uuid);
        if (tag == null) {
            return null;
        }
        if (tag.expireAt() <= System.currentTimeMillis()) {
            combatTags.remove(uuid);
            return null;
        }
        return tag;
    }

    private void removeReciprocal(CombatTag tag) {
        CombatTag opponentTag = combatTags.get(tag.opponent());
        if (opponentTag != null && tag.opponent().equals(opponentTag.opponent())) {
            combatTags.remove(tag.opponent());
            clearBossBar(tag.opponent());
        }
    }

    private record CombatTag(UUID opponent, long expireAt) { }

    private void startBossBarTask() {
        bossBarTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
            for (UUID uuid : new HashSet<>(combatTags.keySet())) {
                CombatTag tag = combatTags.get(uuid);
                if (tag == null) {
                    continue;
                }
                if (tag.expireAt() <= now) {
                    combatTags.remove(uuid);
                    clearBossBar(uuid);
                    continue;
                }
                updateBossBar(uuid);
            }

            // Limpia barras sin tag activo
            for (UUID uuid : new HashSet<>(bossBars.keySet())) {
                if (!combatTags.containsKey(uuid)) {
                    clearBossBar(uuid);
                }
            }
        }, 0L, 10L);
    }

    private void updateBossBar(UUID playerId) {
        CombatTag tag = getActiveTag(playerId);
        if (tag == null) {
            clearBossBar(playerId);
            return;
        }

        Player player = Bukkit.getPlayer(playerId);
        if (player == null || !player.isOnline()) {
            clearBossBar(playerId);
            return;
        }

        String opponentName = "?";
        Player onlineOpp = Bukkit.getPlayer(tag.opponent());
        if (onlineOpp != null) {
            opponentName = onlineOpp.getName();
        } else {
            opponentName = Bukkit.getOfflinePlayer(tag.opponent()).getName();
            if (opponentName == null) {
                opponentName = "Desconocido";
            }
        }

        long remainingMs = Math.max(0, tag.expireAt() - System.currentTimeMillis());
        int remainingSeconds = (int) Math.ceil(remainingMs / 1000.0);
        double progress = Math.min(1.0, Math.max(0.0, remainingMs / (settings.combatCooldownSeconds * 1000.0)));

        BossBar bar = bossBars.computeIfAbsent(playerId, id -> {
            BossBar b = Bukkit.createBossBar("", BarColor.RED, BarStyle.SEGMENTED_10);
            b.addPlayer(player);
            return b;
        });

        bar.setVisible(true);
        bar.setTitle("En combate vs " + opponentName + " · " + remainingSeconds + "s");
        bar.setProgress(progress);
    }

    private void clearBossBar(UUID playerId) {
        BossBar bar = bossBars.remove(playerId);
        if (bar != null) {
            bar.removeAll();
        }
    }

    public int getRemainingCombatSeconds(UUID uuid) {
        CombatTag tag = getActiveTag(uuid);
        if (tag == null) {
            return 0;
        }
        long remainingMs = Math.max(0, tag.expireAt() - System.currentTimeMillis());
        return (int) Math.ceil(remainingMs / 1000.0);
    }

    public String getCombatOpponentName(UUID uuid) {
        CombatTag tag = getActiveTag(uuid);
        if (tag == null) {
            return "";
        }
        Player onlineOpp = Bukkit.getPlayer(tag.opponent());
        if (onlineOpp != null) {
            return onlineOpp.getName();
        }
        String name = Bukkit.getOfflinePlayer(tag.opponent()).getName();
        return name != null ? name : "";
    }

    private static final class Settings {
        private final boolean combatLogEnabled;
        private final int combatCooldownSeconds;
        private final boolean broadcastOnKill;

        private Settings(ConfigurationSection section) {
            this.combatLogEnabled = section == null || section.getBoolean("combatlog_enabled", true);
            this.combatCooldownSeconds = section != null ? section.getInt("combat_cooldown_seconds", 20) : 20;
            this.broadcastOnKill = section == null || section.getBoolean("broadcast_on_kill", true);
        }
    }
}
