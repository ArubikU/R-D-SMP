package net.rollanddeath.smp.core.combat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatLogManager implements Listener {

    private final RollAndDeathSMP plugin;
    private final ReanimationManager reanimationManager;
    private final Settings settings;
    private final Map<UUID, CombatTag> combatTags = new HashMap<>();

    public CombatLogManager(RollAndDeathSMP plugin, ReanimationManager reanimationManager) {
        this.plugin = plugin;
        this.reanimationManager = reanimationManager;
        this.settings = new Settings(plugin.getConfig().getConfigurationSection("deathmatch"));
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
            return;
        }

        combatTags.remove(player.getUniqueId());
        removeReciprocal(tag);

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
        }
    }

    private record CombatTag(UUID opponent, long expireAt) { }

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
