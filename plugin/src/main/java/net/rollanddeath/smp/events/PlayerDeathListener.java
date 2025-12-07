package net.rollanddeath.smp.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.LifeManager;
import net.rollanddeath.smp.integration.discord.DiscordWebhookService;
import net.rollanddeath.smp.core.game.KillPointsManager;
import net.rollanddeath.smp.core.modifiers.ModifierManager;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.Chest.Type;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class PlayerDeathListener implements Listener {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withLocale(new Locale("es", "ES"));

    private final RollAndDeathSMP plugin;
    private final LifeManager lifeManager;
    private final NamespacedKey headDataKey;
    private final Gson gson;
    private final DiscordWebhookService discordService;
    private final KillPointsManager killPointsManager;
    private final ModifierManager modifierManager;
    private final Set<UUID> recentDeaths = new HashSet<>();

    public PlayerDeathListener(RollAndDeathSMP plugin, LifeManager lifeManager, DiscordWebhookService discordService, KillPointsManager killPointsManager, ModifierManager modifierManager) {
        this.plugin = plugin;
        this.lifeManager = lifeManager;
        this.headDataKey = new NamespacedKey(plugin, "death_head");
        this.gson = new GsonBuilder().create();
        this.discordService = discordService;
        this.killPointsManager = killPointsManager;
        this.modifierManager = modifierManager;
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        UUID uuid = player.getUniqueId();
        if (!recentDeaths.add(uuid)) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> recentDeaths.remove(uuid), 20L);
        addDeathHeadDrop(event, player);
        dropDeathChest(event);

        // Kill points for player-versus-player
        if (killer != null && !killer.getUniqueId().equals(player.getUniqueId()) && killPointsManager != null && killPointsManager.isKillPointsEnabled()) {
            int total = killPointsManager.addKill(killer.getUniqueId());
            killer.sendMessage(Component.text("Killpoints: +1 (" + total + ")", NamedTextColor.GOLD));
        }
        
        // Check Permadeath (Day 31+)
        if (plugin.getGameManager().isPermadeathActive()) {
            Component banMsg = Component.text("¡Has muerto durante la MUERTE PERMANENTE!", NamedTextColor.DARK_RED);
            // Ban permanently
            Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "Muerte Permanente (Día 31+)", null, "RollAndDeath");
            player.kick(banMsg);
            
            Component announcement = Component.text("☠ ", NamedTextColor.DARK_RED)
                .append(Component.text(player.getName(), NamedTextColor.RED))
                .append(Component.text(" ha muerto permanentemente.", NamedTextColor.GRAY));
            Bukkit.broadcast(announcement);
            return;
        }

        lifeManager.removeLife(player);
        int remainingLives = lifeManager.getLives(player);

        Component message = Component.text(player.getName())
                .append(Component.text(" ha muerto. Vidas restantes: ", NamedTextColor.GRAY))
                .append(Component.text(remainingLives, remainingLives > 0 ? NamedTextColor.GREEN : NamedTextColor.RED));
        
        event.deathMessage(message);

        if (discordService != null && discordService.isEnabled()) {
            discordService.sendDeathMessage(player, message);
        }

        if (remainingLives <= 0) {
            // Ban logic or Spectator logic
            Component eliminationMsg = Component.text("¡")
                    .append(Component.text(player.getName(), NamedTextColor.RED))
                    .append(Component.text(" ha sido ELIMINADO del SMP!", NamedTextColor.DARK_RED));
            player.getServer().sendMessage(eliminationMsg);
        }
    }

    private void addDeathHeadDrop(PlayerDeathEvent event, Player victim) {
        HeadData data = createHeadData(event, victim);
        ItemStack head = buildHeadItem(data);
        event.getDrops().add(head);
    }

    private void dropDeathChest(PlayerDeathEvent event) {
        if (modifierManager != null && modifierManager.isActive("Mundo Gigante")) {
            return;
        }

        var drops = event.getDrops();
        if (drops.isEmpty()) {
            return;
        }

        var loc = event.getEntity().getLocation();
        var world = loc.getWorld();
        if (world == null) return;

        Block chestBlock = loc.getBlock();
        if (!chestBlock.getType().isAir()) {
            chestBlock = loc.clone().add(0, 1, 0).getBlock();
            if (!chestBlock.getType().isAir()) {
                return; // fallback to vanilla drops
            }
        }

        chestBlock.setType(Material.CHEST);
        if (chestBlock.getState() instanceof Chest chest) {
            chest.customName(Component.text("Restos de " + event.getEntity().getName(), NamedTextColor.RED));
            for (ItemStack item : new ArrayList<>(drops)) {
                Map<Integer, ItemStack> leftover = chest.getBlockInventory().addItem(item);
                leftover.values().forEach(stack -> world.dropItemNaturally(chest.getLocation().add(0.5, 1, 0.5), stack));
            }
            chest.update();
            drops.clear();
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (lifeManager.isEliminated(player)) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(Component.text("Has perdido todas tus vidas. Ahora eres un espectador.", NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Component joinMsg = Component.text("➕ ", NamedTextColor.GREEN)
            .append(Component.text(player.getName(), NamedTextColor.YELLOW))
            .append(Component.text(" se unió al servidor.", NamedTextColor.GRAY));
        event.joinMessage(joinMsg);
        // Initialize lives if new
        lifeManager.getLives(player);
        
        // Check for pending revives
        lifeManager.checkPendingRevive(player);

        if (lifeManager.isEliminated(player)) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(Component.text("Sigues en el Limbo. Espera a ser revivido.", NamedTextColor.GRAY));
        }

        if (discordService != null && discordService.isEnabled()) {
            discordService.sendPlayerJoin(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Component quitMsg = Component.text("➖ ", NamedTextColor.RED)
                .append(Component.text(player.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" salió del servidor.", NamedTextColor.GRAY));
        event.quitMessage(quitMsg);
        if (discordService != null && discordService.isEnabled()) {
            discordService.sendPlayerQuit(player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHeadPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item.getType() != Material.PLAYER_HEAD && item.getType() != Material.PLAYER_WALL_HEAD) {
            return;
        }

        SkullMeta meta = item.hasItemMeta() && item.getItemMeta() instanceof SkullMeta skullMeta ? skullMeta : null;
        if (meta == null) {
            return;
        }
        PersistentDataContainer itemContainer = meta.getPersistentDataContainer();
        if (!itemContainer.has(headDataKey, PersistentDataType.STRING)) {
            return;
        }

        Block block = event.getBlockPlaced();
        if (!(block.getState() instanceof Skull skull)) {
            return;
        }

        String json = itemContainer.get(headDataKey, PersistentDataType.STRING);
        if (json == null) {
            return;
        }

        skull.getPersistentDataContainer().set(headDataKey, PersistentDataType.STRING, json);
        skull.update(true, false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onHeadBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.PLAYER_HEAD && block.getType() != Material.PLAYER_WALL_HEAD) {
            return;
        }

        if (!(block.getState() instanceof Skull skull)) {
            return;
        }

        PersistentDataContainer container = skull.getPersistentDataContainer();
        if (!container.has(headDataKey, PersistentDataType.STRING)) {
            return;
        }

        String json = container.get(headDataKey, PersistentDataType.STRING);
        if (json == null) {
            return;
        }

        HeadData data = gson.fromJson(json, HeadData.class);
        if (data == null) {
            return;
        }

        event.setDropItems(false);
        block.getWorld().dropItemNaturally(block.getLocation(), buildHeadItem(data));
    }

    private HeadData createHeadData(PlayerDeathEvent event, Player victim) {
        Player killer = victim.getKiller();
        EntityDamageEvent cause = victim.getLastDamageCause();

        String causeText = cause != null ? formatCause(cause, killer) : "Desconocida";
        String killerName = killer != null ? killer.getName() : null;
        UUID killerId = killer != null ? killer.getUniqueId() : null;
        String weaponName = killer != null ? describeWeapon(killer.getInventory().getItemInMainHand()) : null;

        return new HeadData(
                victim.getUniqueId(),
                victim.getName(),
                killerId,
                killerName,
                weaponName,
                causeText,
                System.currentTimeMillis()
        );
    }

    private ItemStack buildHeadItem(HeadData data) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        OfflinePlayer owner = Bukkit.getOfflinePlayer(data.victimId());
        meta.setOwningPlayer(owner);
        meta.displayName(Component.text("Cabeza de " + data.victimName(), NamedTextColor.RED));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Causa: " + data.cause(), NamedTextColor.GRAY));
        if (data.killerName() != null) {
            lore.add(Component.text("Asesino: " + data.killerName(), NamedTextColor.DARK_RED));
        }
        if (data.weaponName() != null) {
            lore.add(Component.text("Arma: " + data.weaponName(), NamedTextColor.DARK_RED));
        }
        lore.add(Component.text("Fecha: " + formatTimestamp(data.timestamp()), NamedTextColor.DARK_GRAY));
        meta.lore(lore);

        meta.getPersistentDataContainer().set(headDataKey, PersistentDataType.STRING, gson.toJson(data));
        head.setItemMeta(meta);
        return head;
    }

    private String formatTimestamp(long millis) {
        LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
        return DATE_FORMATTER.format(time);
    }

    private String formatCause(EntityDamageEvent event, Player killer) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        return switch (cause) {
            case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> killer != null ? "Asesinado cuerpo a cuerpo" : "Ataque cuerpo a cuerpo";
            case PROJECTILE -> killer != null ? "Asesinado a distancia" : "Impacto a distancia";
            case FALL -> "Caída fatal";
            case FIRE -> "Ardió";
            case FIRE_TICK -> "Quemado";
            case MELTING -> "Derretido";
            case LAVA -> "Ahogado en lava";
            case DROWNING -> "Ahogado";
            case SUFFOCATION -> "Asfixiado";
            case VOID -> "Cayó al vacío";
            case ENTITY_EXPLOSION, BLOCK_EXPLOSION -> "Explosión";
            case MAGIC -> "Magia";
            case POISON -> "Envenenado";
            case WITHER -> "Marchitado";
            case CONTACT -> "Daño de contacto";
            case CRAMMING -> "Aplastado";
            case FALLING_BLOCK -> "Aplastado por bloque";
            case THORNS -> "Devolución";
            case DRAGON_BREATH -> "Aliento de dragón";
            case HOT_FLOOR -> "Bloques ardientes";
            case FLY_INTO_WALL -> "Impacto cinético";
            case LIGHTNING -> "Electrocutado";
            case SUICIDE -> "Se quitó la vida";
            case STARVATION -> "Murió de hambre";
            case FREEZE -> "Congelado";
            case SONIC_BOOM -> "Explosión sónica";
            case WORLD_BORDER -> "Límite del mundo";
            case DRYOUT -> "Desecado";
            case CUSTOM -> "Causa especial";
            default -> capitalize(cause.name().replace('_', ' ').toLowerCase(Locale.ROOT));
        };
    }

    private String describeWeapon(ItemStack weapon) {
        if (weapon == null || weapon.getType() == Material.AIR) {
            return "Puños";
        }
        if (weapon.hasItemMeta() && weapon.getItemMeta().hasDisplayName()) {
            Component display = weapon.getItemMeta().displayName();
            if (display != null) {
                return PlainTextComponentSerializer.plainText().serialize(display);
            }
        }
        String materialName = weapon.getType().name().toLowerCase(Locale.ROOT).replace('_', ' ');
        return capitalize(materialName);
    }

    private String capitalize(String text) {
        if (text.isEmpty()) {
            return text;
        }
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    private record HeadData(UUID victimId, String victimName, UUID killerId, String killerName, String weaponName, String cause, long timestamp) {}
}
