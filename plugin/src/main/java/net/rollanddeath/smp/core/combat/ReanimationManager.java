package net.rollanddeath.smp.core.combat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.LifeManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ReanimationManager implements Listener {

    private static final double REVIVE_RANGE_SQUARED = 9.0D;

    private final RollAndDeathSMP plugin;
    private final LifeManager lifeManager;
    private final Settings settings;
    private final Map<UUID, DownedPlayer> downedPlayers = new HashMap<>();
    private final Set<UUID> deathBypass = new HashSet<>();

    public ReanimationManager(RollAndDeathSMP plugin, LifeManager lifeManager) {
        this.plugin = plugin;
        this.lifeManager = lifeManager;
        this.settings = new Settings(plugin.getConfig().getConfigurationSection("reanimation"));
    }

    public boolean isEnabled() {
        return settings.enabled;
    }

    public boolean isDowned(Player player) {
        return downedPlayers.containsKey(player.getUniqueId());
    }

    public void markForNaturalDeath(Player player) {
        if (player != null) {
            deathBypass.add(player.getUniqueId());
        }
    }

    public void forceKill(Player player, Component reason) {
        if (player == null) {
            return;
        }
        markForNaturalDeath(player);
        if (reason != null) {
            player.sendMessage(reason);
        }
        clearDownedState(player, true);
        player.setHealth(0.0D);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        UUID uuid = player.getUniqueId();
        DownedPlayer downed = downedPlayers.get(uuid);
        if (downed != null) {
            event.setCancelled(true);
            if (event.getCause() != EntityDamageEvent.DamageCause.CUSTOM && settings.damageTimePenaltySeconds > 0) {
                downed.reduceTime(settings.damageTimePenaltySeconds * 20);
                player.sendActionBar(Component.text("Tiempo restante: " + downed.getSecondsRemaining() + "s", NamedTextColor.RED));
            }
            if (downed.isExpired()) {
                forceBleedOut(player);
            }
            return;
        }

        if (!settings.enabled || deathBypass.remove(uuid)) {
            return;
        }

        if (!shouldDownPlayer(player)) {
            return;
        }

        double healthAfterDamage = player.getHealth() - event.getFinalDamage();
        if (healthAfterDamage > 0.0D) {
            return;
        }

        if (plugin.getGameManager().isPermadeathActive()) {
            return;
        }

        startDowned(player);
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (downedPlayers.containsKey(player.getUniqueId())) {
            forceBleedOut(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        clearDownedState(player, true);
        deathBypass.remove(player.getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Entity clicked = event.getRightClicked();
        if (!(clicked instanceof Player target)) {
            return;
        }

        Player rescuer = event.getPlayer();
        if (rescuer.equals(target)) {
            return;
        }

        DownedPlayer downed = downedPlayers.get(target.getUniqueId());
        if (downed == null) {
            return;
        }

        if (settings.carryEnabled && !rescuer.isSneaking()) {
            toggleCarry(rescuer, target, downed);
            return;
        }

        if (settings.sneakRequired && !rescuer.isSneaking()) {
            rescuer.sendMessage(Component.text("Debes agacharte para reanimar.", NamedTextColor.RED));
            return;
        }

        startRevive(rescuer, target, downed);
    }

    private boolean shouldDownPlayer(Player player) {
        if (!player.isValid() || player.isDead()) {
            return false;
        }
        if (player.getGameMode() == GameMode.SPECTATOR) {
            return false;
        }
        if (lifeManager.isEliminated(player)) {
            return false;
        }
        return true;
    }

    private void startDowned(Player player) {
        UUID uuid = player.getUniqueId();
        if (downedPlayers.containsKey(uuid)) {
            return;
        }

        DownedPlayer state = new DownedPlayer(player, settings.bleedoutSeconds * 20);
        downedPlayers.put(uuid, state);

        player.setHealth(1.0D);
        player.setFoodLevel(Math.min(player.getFoodLevel(), 8));
        player.setInvulnerable(true);
        player.setWalkSpeed(Math.max(0.02F, player.getWalkSpeed() * 0.25F));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 6, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 2, false, false, false));
        player.sendTitle("¡HERIDO!", "Esperando reanimación", 10, 40, 20);
        Bukkit.broadcast(Component.text(player.getName() + " ha quedado incapacitado.", NamedTextColor.RED));

        state.startCountdown(player);
    }

    private void startRevive(Player rescuer, Player target, DownedPlayer downed) {
        if (downed.hasReviveSession()) {
            downed.joinRevive(rescuer, target, settings.sneakRequired);
            return;
        }

        downed.beginRevive(rescuer, target, settings.reviveChannelSeconds * 20, settings.sneakRequired);
    }

    private void toggleCarry(Player rescuer, Player target, DownedPlayer downed) {
        if (target.isInsideVehicle()) {
            target.leaveVehicle();
            downed.clearCarrier();
            rescuer.sendMessage(Component.text("Has soltado a " + target.getName() + ".", NamedTextColor.GRAY));
            return;
        }

        if (!rescuer.getPassengers().isEmpty()) {
            rescuer.sendMessage(Component.text("Ya estás cargando a alguien.", NamedTextColor.RED));
            return;
        }

        rescuer.addPassenger(target);
        downed.setCarrier(rescuer.getUniqueId());
        rescuer.sendMessage(Component.text("Estás cargando a " + target.getName() + ".", NamedTextColor.GREEN));
    }

    private void finishRevive(Player target, Player rescuer, DownedPlayer downed) {
        clearDownedState(target, false);
        double maxHealth = target.getMaxHealth();
        double restore = Math.max(2.0D, Math.min(settings.revivedHealth, maxHealth));
        target.setHealth(restore);
        target.setInvulnerable(false);
        target.setWalkSpeed(downed.originalWalkSpeed);
        target.removePotionEffect(PotionEffectType.SLOWNESS);
        target.removePotionEffect(PotionEffectType.WEAKNESS);
        target.resetTitle();
        target.sendMessage(Component.text("¡Reanimado por " + rescuer.getName() + "!", NamedTextColor.GREEN));
        rescuer.sendMessage(Component.text("Has reanimado a " + target.getName() + ".", NamedTextColor.GOLD));
    }

    private void forceBleedOut(Player player) {
        clearDownedState(player, true);
        player.setInvulnerable(false);
        markForNaturalDeath(player);
        player.sendMessage(Component.text("Has desangrado...", NamedTextColor.DARK_RED));
        player.setHealth(0.0D);
    }

    private void clearDownedState(Player player, boolean silent) {
        if (player == null) {
            return;
        }

        DownedPlayer downed = downedPlayers.remove(player.getUniqueId());
        if (downed == null) {
            return;
        }

        downed.cancelAll();
        player.setInvulnerable(false);
        player.setWalkSpeed(downed.originalWalkSpeed);
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
        if (player.isInsideVehicle()) {
            player.leaveVehicle();
        }
        if (!silent) {
            player.resetTitle();
        }
    }

    private static final class Settings {
        private final boolean enabled;
        private final int bleedoutSeconds;
        private final int reviveChannelSeconds;
        private final int damageTimePenaltySeconds;
        private final boolean carryEnabled;
        private final boolean sneakRequired;
        private final double revivedHealth;

        private Settings(ConfigurationSection section) {
            this.enabled = section == null || section.getBoolean("enabled", true);
            this.bleedoutSeconds = section != null ? section.getInt("bleedout_seconds", 100) : 100;
            this.reviveChannelSeconds = section != null ? section.getInt("revive_channel_seconds", 15) : 15;
            this.damageTimePenaltySeconds = section != null ? section.getInt("damage_time_penalty_seconds", 10) : 10;
            this.carryEnabled = section == null || section.getBoolean("carry_enabled", true);
            this.sneakRequired = section == null || section.getBoolean("sneak_required", true);
            this.revivedHealth = section != null ? section.getDouble("revived_health", 10.0D) : 10.0D;
        }
    }

    private final class DownedPlayer {
        private final UUID targetId;
        private final float originalWalkSpeed;
        private final int maxTicks;
        private int ticksRemaining;
        private BukkitTask countdownTask;
        private ReviveSession reviveSession;
        private UUID carrier;

        private DownedPlayer(Player player, int ticksRemaining) {
            this.targetId = player.getUniqueId();
            this.originalWalkSpeed = player.getWalkSpeed();
            this.maxTicks = ticksRemaining;
            this.ticksRemaining = ticksRemaining;
        }

        private void startCountdown(Player player) {
            countdownTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cancel();
                        return;
                    }
                    ticksRemaining = Math.max(0, ticksRemaining - 20);
                    player.sendActionBar(Component.text("Reanimación en " + (ticksRemaining / 20) + "s", NamedTextColor.RED));
                    if (ticksRemaining <= 0) {
                        cancel();
                        forceBleedOut(player);
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }

        private boolean hasReviveSession() {
            return reviveSession != null;
        }

        private void beginRevive(Player rescuer, Player target, int requiredTicks, boolean sneakRequired) {
            reviveSession = new ReviveSession(rescuer, target, requiredTicks, sneakRequired);
            reviveSession.start();
        }

        private void joinRevive(Player rescuer, Player target, boolean sneakRequired) {
            if (reviveSession != null) {
                reviveSession.addRescuer(rescuer, target, sneakRequired);
            }
        }

        private void reduceTime(int ticks) {
            ticksRemaining = Math.max(0, ticksRemaining - ticks);
        }

        private boolean isExpired() {
            return ticksRemaining <= 0;
        }

        private int getSecondsRemaining() {
            return ticksRemaining / 20;
        }

        private void setCarrier(UUID carrier) {
            this.carrier = carrier;
        }

        private void clearCarrier() {
            this.carrier = null;
        }

        private void cancelAll() {
            if (countdownTask != null) {
                countdownTask.cancel();
            }
            if (reviveSession != null) {
                reviveSession.terminate(false);
                reviveSession = null;
            }
                if (carrier != null) {
                Player holder = Bukkit.getPlayer(carrier);
                Player target = Bukkit.getPlayer(targetId);
                if (holder != null && target != null) {
                    holder.removePassenger(target);
                }
                carrier = null;
            }
        }
    }

    private final class ReviveSession {
        private final UUID targetId;
        private final boolean sneakRequired;
        private int remainingTicks;
        private BukkitTask task;
        private final Set<UUID> rescuers = new HashSet<>();

        private ReviveSession(Player rescuer, Player target, int ticks, boolean sneakRequired) {
            this.targetId = target.getUniqueId();
            this.remainingTicks = ticks;
            this.sneakRequired = sneakRequired;
            rescuers.add(rescuer.getUniqueId());
            rescuer.sendMessage(Component.text("Comienzas a reanimar a " + target.getName() + ".", NamedTextColor.GOLD));
            target.sendMessage(Component.text(rescuer.getName() + " está intentando reanimarte.", NamedTextColor.YELLOW));
        }

        private void start() {
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    Player target = Bukkit.getPlayer(targetId);
                    DownedPlayer downed = downedPlayers.get(targetId);
                    if (target == null || downed == null) {
                        terminate(true);
                        return;
                    }

                    Set<UUID> invalid = new HashSet<>();
                    List<Player> activeRescuers = new ArrayList<>();
                    for (UUID rescuerId : rescuers) {
                        Player rescuer = Bukkit.getPlayer(rescuerId);
                        if (rescuer == null || !rescuer.isOnline()) {
                            invalid.add(rescuerId);
                            continue;
                        }
                        if (sneakRequired && !rescuer.isSneaking()) {
                            rescuer.sendMessage(Component.text("Has dejado de reanimar.", NamedTextColor.RED));
                            invalid.add(rescuerId);
                            continue;
                        }
                        if (rescuer.getLocation().distanceSquared(target.getLocation()) > REVIVE_RANGE_SQUARED) {
                            rescuer.sendMessage(Component.text("Te has alejado demasiado.", NamedTextColor.RED));
                            invalid.add(rescuerId);
                            continue;
                        }
                        activeRescuers.add(rescuer);
                    }

                    if (!invalid.isEmpty()) {
                        rescuers.removeAll(invalid);
                    }

                    if (activeRescuers.isEmpty()) {
                        terminate(true);
                        target.sendMessage(Component.text("La reanimación ha sido interrumpida.", NamedTextColor.RED));
                        target.sendActionBar(Component.empty());
                        return;
                    }

                    double multiplier = 1.0 + Math.max(0, activeRescuers.size() - 1) * 0.2;
                    int decrement = Math.max(1, (int) Math.round(5 * multiplier));
                    remainingTicks = Math.max(0, remainingTicks - decrement);

                    int secondsLeft = Math.max(0, remainingTicks / 20);
                    target.sendActionBar(Component.text("Siendo reanimado... " + secondsLeft + "s", NamedTextColor.YELLOW));
                    for (Player rescuer : activeRescuers) {
                        rescuer.sendActionBar(Component.text("Reanimando... " + secondsLeft + "s", NamedTextColor.GOLD));
                    }

                    if (remainingTicks <= 0) {
                        terminate(false);
                        DownedPlayer sessionDowned = downedPlayers.get(targetId);
                        if (sessionDowned != null) {
                            sessionDowned.reviveSession = null;
                        }
                        Player primary = activeRescuers.get(0);
                        finishRevive(target, primary, downed);
                        Component success = Component.text("Reanimación completada.", NamedTextColor.GREEN);
                        for (Player rescuer : activeRescuers) {
                            rescuer.sendMessage(Component.text("Has ayudado a reanimar a " + target.getName() + ".", NamedTextColor.GOLD));
                            rescuer.sendActionBar(Component.empty());
                        }
                        target.sendMessage(success);
                        target.sendActionBar(Component.empty());
                    }
                }
            }.runTaskTimer(plugin, 0L, 5L);
        }

        private void terminate(boolean clearSession) {
            if (task != null) {
                task.cancel();
            }
            if (clearSession) {
                DownedPlayer downed = downedPlayers.get(targetId);
                if (downed != null) {
                    downed.reviveSession = null;
                }
            }
        }

        private void cancelWithMessage(Player rescuer, Component message) {
            terminate(true);
            rescuer.sendMessage(message);
        }

        private void addRescuer(Player rescuer, Player target, boolean requireSneak) {
            if (rescuer == null) {
                return;
            }
            if (rescuers.contains(rescuer.getUniqueId())) {
                rescuer.sendMessage(Component.text("Ya estás ayudando con la reanimación.", NamedTextColor.YELLOW));
                return;
            }
            if (requireSneak && !rescuer.isSneaking()) {
                rescuer.sendMessage(Component.text("Debes agacharte para ayudar a reanimar.", NamedTextColor.RED));
                return;
            }
            if (rescuer.getLocation().distanceSquared(target.getLocation()) > REVIVE_RANGE_SQUARED) {
                rescuer.sendMessage(Component.text("Acércate un poco más.", NamedTextColor.RED));
                return;
            }

            rescuers.add(rescuer.getUniqueId());
            rescuer.sendMessage(Component.text("Te unes a la reanimación de " + target.getName() + ".", NamedTextColor.GOLD));
            target.sendMessage(Component.text(rescuer.getName() + " se ha unido a tu reanimación.", NamedTextColor.YELLOW));
        }
    }
}
