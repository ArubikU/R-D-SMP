package net.rollanddeath.smp.core.combat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.LifeManager;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
    private final Map<UUID, CarrierCharge> carrierCharges = new HashMap<>();
    private final Set<UUID> slowedCarriers = new HashSet<>();
    private BukkitTask carrierChargeTask;
    private long tickCounter;

    public ReanimationManager(RollAndDeathSMP plugin, LifeManager lifeManager) {
        this.plugin = plugin;
        this.lifeManager = lifeManager;
        this.settings = new Settings(plugin.getConfig().getConfigurationSection("reanimation"));
        startCarrierTask();
    }

    private void startCarrierTask() {
        carrierChargeTask = new BukkitRunnable() {
            @Override
            public void run() {
                tickCounter++;
                tickCarrierCharges();
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    public void shutdown() {
        if (carrierChargeTask != null) {
            carrierChargeTask.cancel();
        }
        carrierCharges.clear();
        for (UUID uuid : new HashSet<>(slowedCarriers)) {
            Player carrier = Bukkit.getPlayer(uuid);
            if (carrier != null) {
                carrier.removePotionEffect(PotionEffectType.SLOWNESS);
            }
        }
        slowedCarriers.clear();
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
        if (player.isDead()) {
            return;
        }
        UUID uuid = player.getUniqueId();
        if (deathBypass.contains(uuid)) {
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

        if (hasResurrectionTotem(player)) {
            return;
        }

        if (plugin.getGameManager().isPermadeathActive()) {
            return;
        }

        startDowned(player);
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        if (downedPlayers.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
        }
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

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        DownedPlayer downed = findCarriedDowned(player.getUniqueId());
        if (downed == null) {
            return;
        }

        CarrierCharge charge = carrierCharges.computeIfAbsent(player.getUniqueId(), id -> new CarrierCharge(downed.getTargetId(), tickCounter));
        charge.updateTarget(downed.getTargetId());
        charge.markInteract(tickCounter);

        event.setCancelled(true);
        event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
        event.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
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

        state.applyDownedPose(player, settings.downedPose);
        state.startCountdown(player);
        state.startBleedingEffect(player);
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
            dropCarriedPlayer(rescuer, target, downed);
            return;
        }

        if (!rescuer.getPassengers().isEmpty()) {
            rescuer.sendMessage(Component.text("Ya estás cargando a alguien.", NamedTextColor.RED));
            return;
        }

        startCarrying(rescuer, target, downed);
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
        if (player == null) {
            return;
        }
        if (player.isDead()) {
            return;
        }
        UUID uuid = player.getUniqueId();
        if (deathBypass.contains(uuid)) {
            return;
        }
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
        downed.restorePose(player);
        if (!silent) {
            player.resetTitle();
        }
    }

    private void startCarrying(Player carrier, Player target, DownedPlayer downed) {
        carrier.addPassenger(target);
        downed.setCarrier(carrier.getUniqueId());
        slowedCarriers.add(carrier.getUniqueId());
        carrier.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 0, false, false, false));
        carrier.sendMessage(Component.text("Estás cargando a " + target.getName() + ".", NamedTextColor.GREEN));
    }

    private void dropCarriedPlayer(Player carrier, Player target, DownedPlayer downed) {
        target.leaveVehicle();
        downed.clearCarrier();
        clearCarrierEffects(carrier);
        carrier.sendMessage(Component.text("Has soltado a " + target.getName() + ".", NamedTextColor.GRAY));
    }

    private void throwCarriedPlayer(Player carrier, DownedPlayer downed, double distance) {
        Player target = Bukkit.getPlayer(downed.getTargetId());
        if (target == null) {
            clearCarrierEffects(carrier);
            downed.clearCarrier();
            return;
        }

        clearCarrierEffects(carrier);
        carrier.removePassenger(target);
        downed.clearCarrier();

        double clampedDistance = Math.min(5.0D, Math.max(1.5D, distance));
        Vector direction = carrier.getLocation().getDirection();
        if (direction.lengthSquared() == 0.0D) {
            direction = carrier.getEyeLocation().getDirection();
        }

        Vector horizontal = direction.clone().setY(0.0D);
        if (horizontal.lengthSquared() == 0.0D) {
            horizontal = new Vector(0.0D, 0.0D, 0.0D);
        } else {
            horizontal = horizontal.normalize().multiply(clampedDistance * 0.45D);
        }

        double vertical = 0.25D + (clampedDistance / 5.0D) * 0.45D;
        Vector velocity = horizontal.add(new Vector(0.0D, vertical, 0.0D));
        target.setVelocity(velocity);

        carrier.sendMessage(Component.text("Lanzas a " + target.getName() + " a " + String.format(Locale.ROOT, "%.1f", clampedDistance) + " bloques.", NamedTextColor.GRAY));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Player refreshed = Bukkit.getPlayer(downed.getTargetId());
            if (refreshed != null && downedPlayers.containsKey(downed.getTargetId())) {
                downed.ensurePose(refreshed);
            }
        }, 2L);
    }

    private void clearCarrierEffects(Player carrier) {
        if (carrier == null) {
            return;
        }
        if (slowedCarriers.remove(carrier.getUniqueId())) {
            carrier.removePotionEffect(PotionEffectType.SLOWNESS);
        }
        carrierCharges.remove(carrier.getUniqueId());
    }

    private DownedPlayer findCarriedDowned(UUID carrierId) {
        for (DownedPlayer downed : downedPlayers.values()) {
            if (carrierId.equals(downed.getCarrierId())) {
                return downed;
            }
        }
        return null;
    }

    private void tickCarrierCharges() {
        if (carrierCharges.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<UUID, CarrierCharge>> iterator = carrierCharges.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, CarrierCharge> entry = iterator.next();
            UUID carrierId = entry.getKey();
            CarrierCharge charge = entry.getValue();
            DownedPlayer downed = downedPlayers.get(charge.getTargetId());
            Player carrier = Bukkit.getPlayer(carrierId);

            if (carrier == null || downed == null || !carrierId.equals(downed.getCarrierId())) {
                iterator.remove();
                if (carrier != null) {
                    clearCarrierEffects(carrier);
                }
                continue;
            }

            charge.tick(tickCounter);

            if (charge.isCharging(tickCounter)) {
                double displayDistance = charge.getChargedDistance();
                carrier.sendActionBar(Component.text(String.format(Locale.ROOT, "Cargando lanzamiento: %.1f bloques", displayDistance), NamedTextColor.GOLD));
            }

            if (charge.shouldRelease(tickCounter)) {
                double distance = charge.getChargedDistance();
                throwCarriedPlayer(carrier, downed, distance);
                iterator.remove();
            }
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
        private final Pose downedPose;

        private Settings(ConfigurationSection section) {
            this.enabled = section == null || section.getBoolean("enabled", true);
            this.bleedoutSeconds = section != null ? section.getInt("bleedout_seconds", 100) : 100;
            this.reviveChannelSeconds = section != null ? section.getInt("revive_channel_seconds", 15) : 15;
            this.damageTimePenaltySeconds = section != null ? section.getInt("damage_time_penalty_seconds", 10) : 10;
            this.carryEnabled = section == null || section.getBoolean("carry_enabled", true);
            this.sneakRequired = section == null || section.getBoolean("sneak_required", true);
            this.revivedHealth = section != null ? section.getDouble("revived_health", 10.0D) : 10.0D;
            String poseName = section != null ? section.getString("downed_pose", "SLEEPING") : "SLEEPING";
            this.downedPose = parsePose(poseName);
        }

        private Pose parsePose(String raw) {
            if (raw == null) {
                return Pose.SLEEPING;
            }
            String normalized = raw.trim().toUpperCase(Locale.ROOT);
            if (normalized.equals("SWIMMING")) {
                return Pose.SWIMMING;
            }
            if (normalized.equals("SLEEPING")) {
                return Pose.SLEEPING;
            }
            return Pose.SLEEPING;
        }
    }

    private final class DownedPlayer {
        private final UUID targetId;
        private final float originalWalkSpeed;
        private final int maxTicks;
        private int ticksRemaining;
        private BukkitTask countdownTask;
        private BukkitTask bleedTask;
        private ReviveSession reviveSession;
        private UUID carrier;
        private final Pose originalPose;
        private final boolean wasSwimming;
        private boolean poseForced;

        private DownedPlayer(Player player, int ticksRemaining) {
            this.targetId = player.getUniqueId();
            this.originalWalkSpeed = player.getWalkSpeed();
            this.maxTicks = ticksRemaining;
            this.ticksRemaining = ticksRemaining;
            this.originalPose = player.getPose();
            this.wasSwimming = player.isSwimming();
            this.poseForced = false;
        }

        private void startCountdown(Player player) {
            countdownTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cancelBleedingTask();
                        cancel();
                        return;
                    }
                    ensurePose(player);
                    ticksRemaining = Math.max(0, ticksRemaining - 20);
                    player.sendActionBar(buildBleedingActionBar());
                    if (ticksRemaining <= 0) {
                        cancel();
                        forceBleedOut(player);
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }

        void startBleedingEffect(Player player) {
            cancelBleedingTask();
            bleedTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cancelBleedingTask();
                        cancel();
                        return;
                    }
                    player.getWorld().spawnParticle(
                        Particle.DUST,
                        player.getLocation().add(0.0D, 0.6D, 0.0D),
                        12,
                        0.35D,
                        0.20D,
                        0.35D,
                        new Particle.DustOptions(Color.fromRGB(170, 0, 0), 1.2F)
                    );
                }
            }.runTaskTimer(plugin, 0L, 10L);
        }

        private void cancelBleedingTask() {
            if (bleedTask != null) {
                bleedTask.cancel();
                bleedTask = null;
            }
        }

        private Component buildBleedingActionBar() {
            return Component.text()
                .append(Component.text("Sangrando", NamedTextColor.DARK_RED))
                .append(Component.text(" · ", NamedTextColor.DARK_GRAY))
                .append(Component.text("Reanimación en " + getSecondsRemaining() + "s", NamedTextColor.RED))
                .build();
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

        private UUID getCarrierId() {
            return carrier;
        }

        private UUID getTargetId() {
            return targetId;
        }

        private void cancelAll() {
            if (countdownTask != null) {
                countdownTask.cancel();
            }
            cancelBleedingTask();
            if (reviveSession != null) {
                reviveSession.terminate(false);
                reviveSession = null;
            }
            if (carrier != null) {
                Player holder = Bukkit.getPlayer(carrier);
                Player target = Bukkit.getPlayer(targetId);
                if (holder != null && target != null) {
                    holder.removePassenger(target);
                    clearCarrierEffects(holder);
                }
                carrier = null;
            }
        }

        private void applyDownedPose(Player player, Pose pose) {
            if (pose == null) {
                return;
            }
            switch (pose) {
                case SLEEPING -> {
                    player.setPose(Pose.SLEEPING);
                    player.setSwimming(false);
                    poseForced = true;
                }
                case SWIMMING -> {
                    player.setPose(Pose.SWIMMING);
                    player.setSwimming(true);
                    poseForced = true;
                }
                default -> poseForced = false;
            }
        }

        private void ensurePose(Player player) {
            if (!poseForced) {
                return;
            }
            if (player.isInsideVehicle()) {
                return;
            }
            Pose desired = settings.downedPose;
            if (desired == null) {
                poseForced = false;
                return;
            }
            if (player.getPose() != desired) {
                player.setPose(desired);
            }
            if (desired == Pose.SWIMMING && !player.isSwimming()) {
                player.setSwimming(true);
            }
        }

        private void restorePose(Player player) {
            if (!poseForced) {
                return;
            }
            player.setPose(originalPose);
            player.setSwimming(wasSwimming);
            poseForced = false;
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

                    downed.ensurePose(target);

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

    private static final class CarrierCharge {
        private static final int MAX_CHARGE_TICKS = 60;
        private static final int RELEASE_IDLE_TICKS = 3;

        private UUID targetId;
        private long lastInteractTick;
        private int chargeTicks;

        private CarrierCharge(UUID targetId, long currentTick) {
            this.targetId = targetId;
            this.lastInteractTick = currentTick;
            this.chargeTicks = 0;
        }

        private void updateTarget(UUID targetId) {
            this.targetId = targetId;
        }

        private UUID getTargetId() {
            return targetId;
        }

        private void markInteract(long tick) {
            this.lastInteractTick = tick;
        }

        private void tick(long currentTick) {
            if (currentTick - lastInteractTick <= 1) {
                chargeTicks = Math.min(MAX_CHARGE_TICKS, chargeTicks + 1);
            }
        }

        private boolean isCharging(long currentTick) {
            return currentTick - lastInteractTick <= 1 && chargeTicks > 0;
        }

        private boolean shouldRelease(long currentTick) {
            return currentTick - lastInteractTick > RELEASE_IDLE_TICKS;
        }

        private double getChargedDistance() {
            double normalized = Math.min(1.0D, chargeTicks / (double) MAX_CHARGE_TICKS);
            return 1.5D + (normalized * 3.5D);
        }
    }

    private boolean hasResurrectionTotem(Player player) {
        return isResurrectionTotem(player.getInventory().getItemInMainHand())
            || isResurrectionTotem(player.getInventory().getItemInOffHand());
    }

    private boolean isResurrectionTotem(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack.getType() != Material.TOTEM_OF_UNDYING) {
            return false;
        }
        return stack.getAmount() > 0;
    }
}
