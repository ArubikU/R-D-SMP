package net.rollanddeath.smp.core.modifiers.scripted;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import net.rollanddeath.smp.core.scripting.ScriptPhase;
import net.rollanddeath.smp.core.scripting.ScriptVars;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class ScriptedModifier extends Modifier {

    private final RollAndDeathSMP rdsmp;
    private final Map<String, ModifierRule> events;
    private BukkitTask tickTask;
    private BukkitTask fastTickTask;

    public ScriptedModifier(JavaPlugin plugin, String name, String type, String description, Map<String, ModifierRule> events) {
        super(plugin, name, type, description);
        this.rdsmp = (RollAndDeathSMP) plugin;
        this.events = events;
    }



    @Override
    public void onEnable() {
        super.onEnable();
        // Hook opcional de enable (se ejecuta una vez por jugador online)
        if (events.containsKey("modifier_enable")) {
            ModifierRule rule = events.get("modifier_enable");
            for (Player player : Bukkit.getOnlinePlayers()) {
                Map<String, Object> vars = ScriptVars.create()
                        .subject(player)
                        .build();
                applyRule(rule, player, "modifier_enable", vars);
            }
        }

        // Tick loop opcional (1 vez/seg)
        if (events.containsKey("player_tick")) {
            tickTask = Bukkit.getScheduler().runTaskTimer(rdsmp, this::onTick, 20L, 20L);
        }

        // Tick rápido opcional (cada 5 ticks) - útil para imán de items, etc.
        if (events.containsKey("player_fast_tick")) {
            fastTickTask = Bukkit.getScheduler().runTaskTimer(rdsmp, this::onFastTick, 5L, 5L);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (tickTask != null && !tickTask.isCancelled()) {
            tickTask.cancel();
        }
        if (fastTickTask != null && !fastTickTask.isCancelled()) {
            fastTickTask.cancel();
        }

        // Hook opcional de disable (se ejecuta una vez por jugador online)
        if (events.containsKey("modifier_disable")) {
            ModifierRule rule = events.get("modifier_disable");
            for (Player player : Bukkit.getOnlinePlayers()) {
                Map<String, Object> vars = ScriptVars.create()
                        .subject(player)
                        .build();
                applyRule(rule, player, "modifier_disable", vars);
            }
        }
    }

    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ModifierRule rule = events.get("player_regain_health");
        if (rule == null) return;

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        boolean deny = applyRule(rule, player, "player_regain_health", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ModifierRule rule = events.get("player_join");
        if (rule == null || player == null) return;

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        applyRule(rule, player, "player_join", vars);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ModifierRule rule = events.get("inventory_click");
        if (rule == null) return;

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        boolean deny = applyRule(rule, player, "inventory_click", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        ModifierRule rule = events.get("inventory_close");
        if (rule == null) return;

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        applyRule(rule, player, "inventory_close", vars);
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        ModifierRule rule = events.get("player_exp_change");
        if (rule == null || player == null) return;

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        boolean deny = applyRule(rule, player, "player_exp_change", vars);
        if (deny) {
            event.setAmount(0);
        }
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        ModifierRule rule = events.get("player_fish");
        if (rule == null || player == null) return;

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        boolean deny = applyRule(rule, player, "player_fish", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ModifierRule rule = events.get("player_pickup_item");
        if (rule == null) return;

        ItemStack stack = null;
        try {
            stack = event.getItem() != null ? event.getItem().getItemStack() : null;
        } catch (Exception ignored) {
            stack = null;
        }

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .item(stack)
                .build();

        boolean deny = applyRule(rule, player, "player_pickup_item", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ModifierRule rule = events.get("block_break");
        if (rule == null) return;

        ItemStack tool = null;
        try {
            tool = player.getInventory().getItemInMainHand();
        } catch (Exception ignored) {
            tool = null;
        }

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .item(tool)
                .build();

        boolean deny = applyRule(rule, player, "block_break", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        ModifierRule rule = events.get("player_portal");
        if (rule == null) return;

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        boolean deny = applyRule(rule, player, "player_portal", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ModifierRule rule = events.get("player_item_consume");
        if (rule == null) return;

        ItemStack item = event.getItem();

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .item(item)
                .build();

        boolean deny = applyRule(rule, player, "player_item_consume", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ModifierRule rule = events.get("food_level_change");
        if (rule == null) return;

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        boolean deny = applyRule(rule, player, "food_level_change", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        ModifierRule rule = events.get("player_bed_enter");
        if (rule == null) return;

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        boolean deny = applyRule(rule, player, "player_bed_enter", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        Player player = event.getEnchanter();
        ModifierRule rule = events.get("prepare_item_enchant");
        if (rule == null) return;

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        boolean deny = applyRule(rule, player, "prepare_item_enchant", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ModifierRule rule = events.get("prepare_anvil");
        if (rule == null) return;

        Player player = event.getViewers().isEmpty() ? null : (event.getViewers().get(0) instanceof Player p ? p : null);
        if (player == null) return;

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        // PrepareAnvilEvent no es cancellable; las acciones deben mutar el evento (ej. clear_anvil_result)
        applyRule(rule, player, "prepare_anvil", vars);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ModifierRule rule = events.get("player_interact");
        if (rule == null) return;

        ItemStack item = event.getItem();
        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .item(item)
                .build();

        boolean deny = applyRule(rule, player, "player_interact", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        ModifierRule rule = events.get("player_chat");
        if (rule == null) return;

        Player player = event.getPlayer();
        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        boolean deny = applyRule(rule, player, "player_chat", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        ModifierRule rule = events.get("potion_splash");
        if (rule == null) return;

        // Usamos el primer jugador afectado como "player" de contexto; si no hay, no ejecutamos.
        Player player = event.getAffectedEntities().stream().filter(e -> e instanceof Player).map(e -> (Player) e).findFirst().orElse(null);
        if (player == null) return;

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        boolean deny = applyRule(rule, player, "potion_splash", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onAreaEffectCloud(AreaEffectCloudApplyEvent event) {
        ModifierRule rule = events.get("area_effect_cloud_apply");
        if (rule == null) return;

        Player player = event.getAffectedEntities().stream().filter(e -> e instanceof Player).map(e -> (Player) e).findFirst().orElse(null);
        if (player == null) return;

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        boolean deny = applyRule(rule, player, "area_effect_cloud_apply", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        ModifierRule rule = events.get("player_move");
        if (rule == null) return;

        if (event.getTo() == null) return;

        // Optimización: solo si cambia de bloque
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
            && event.getFrom().getBlockY() == event.getTo().getBlockY()
            && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        Player player = event.getPlayer();
        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        boolean deny = applyRule(rule, player, "player_move", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        ModifierRule rule = events.get("player_death");
        if (rule == null) return;

        Player player = event.getEntity();
        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        applyRule(rule, player, "player_death", vars);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        ModifierRule rule = events.get("entity_death");
        if (rule == null) return;

        Player player = null;
        try {
            player = event.getEntity() != null ? event.getEntity().getKiller() : null;
        } catch (Exception ignored) {
            player = null;
        }

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .target(event.getEntity())
                .build();

        applyRule(rule, player, "entity_death", vars);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        ModifierRule rule = events.get("entity_spawn");
        if (rule == null) return;

        Entity subject = null;
        try {
            subject = event.getEntity();
        } catch (Exception ignored) {
            subject = null;
        }
        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(subject)
                .build();

        applyRule(rule, null, "entity_spawn", vars);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        ModifierRule rule = events.get("creature_spawn");
        if (rule == null) return;

        Entity subject = null;
        try {
            subject = event.getEntity();
        } catch (Exception ignored) {
            subject = null;
        }
        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(subject)
                .build();

        applyRule(rule, null, "creature_spawn", vars);
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        ModifierRule rule = events.get("explosion_prime");
        if (rule == null) return;

        Entity subject = null;
        try {
            subject = event.getEntity();
        } catch (Exception ignored) {
            subject = null;
        }
        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(subject)
                .build();

        boolean deny = applyRule(rule, null, "explosion_prime", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        ModifierRule rule = events.get("villager_acquire_trade");
        if (rule == null) return;

        Entity subject = null;
        try {
            subject = event.getEntity();
        } catch (Exception ignored) {
            subject = null;
        }
        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(subject)
                .build();

        applyRule(rule, null, "villager_acquire_trade", vars);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        ModifierRule rule = events.get("entity_damage");
        if (rule == null) return;

        Player player = event.getEntity() instanceof Player p ? p : null;

        Entity subject = player != null ? player : event.getEntity();
        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(subject)
                .build();

        boolean deny = applyRule(rule, player, "entity_damage", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        ModifierRule rule = events.get("entity_damage_by_entity");
        if (rule == null) return;

        Player player = event.getEntity() instanceof Player p ? p : null;

        Player damagerPlayer = event.getDamager() instanceof Player p ? p : null;
        ItemStack damagerMain = null;
        ItemStack damagerOff = null;
        try {
            if (damagerPlayer != null) {
                damagerMain = damagerPlayer.getInventory().getItemInMainHand();
                damagerOff = damagerPlayer.getInventory().getItemInOffHand();
            }
        } catch (Exception ignored) {
            damagerMain = null;
            damagerOff = null;
        }

        Entity subject = player != null ? player : event.getEntity();
        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(subject)
                .target(event.getDamager())
                .build();

        boolean deny = applyRule(rule, player, "entity_damage_by_entity", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        ModifierRule rule = events.get("entity_shoot_bow");
        if (rule == null) return;
        if (!(event.getEntity() instanceof Player player)) return;

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        boolean deny = applyRule(rule, player, "entity_shoot_bow", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        ModifierRule rule = events.get("prepare_item_craft");
        if (rule == null) return;

        Player player = null;
        try {
            for (var viewer : event.getViewers()) {
                if (viewer instanceof Player p) {
                    player = p;
                    break;
                }
            }
        } catch (Exception ignored) {
            player = null;
        }

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        applyRule(rule, player, "prepare_item_craft", vars);
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        ModifierRule rule = events.get("player_item_damage");
        if (rule == null) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .item(item)
                .build();

        boolean deny = applyRule(rule, player, "player_item_damage", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    private void onTick() {
        ModifierRule rule = events.get("player_tick");
        if (rule == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("y", player.getLocation().getY());
            try {
                long time = player.getWorld() != null ? player.getWorld().getTime() : 0L;
                eventData.put("world_time", time);
                eventData.put("is_day", (time >= 0L && time < 12300L) || time > 23850L);
            } catch (Exception ignored) {
                eventData.put("world_time", 0L);
                eventData.put("is_day", false);
            }
            try {
                Block b = player.getLocation() != null ? player.getLocation().getBlock() : null;
                eventData.put("sky_light", b != null ? b.getLightFromSky() : null);
            } catch (Exception ignored) {
                eventData.put("sky_light", null);
            }
            try {
                ItemStack helmet = player.getInventory() != null ? player.getInventory().getHelmet() : null;
                eventData.put("helmet_material", helmet != null ? helmet.getType().name() : null);
            } catch (Exception ignored) {
                eventData.put("helmet_material", null);
            }
            try {
                Block block = player.getLocation() != null ? player.getLocation().getBlock() : null;
                eventData.put("block_type", block != null ? block.getType().name() : null);
                Block below = block != null ? block.getRelative(0, -1, 0) : null;
                eventData.put("block_below_type", below != null ? below.getType().name() : null);
            } catch (Exception ignored) {
                eventData.put("block_type", null);
                eventData.put("block_below_type", null);
            }

            Map<String, Object> vars = ScriptVars.create()
                    .event(eventData)
                    .subject(player)
                    .build();

            applyRule(rule, player, "player_tick", vars);
        }
    }

    private void onFastTick() {
        ModifierRule rule = events.get("player_fast_tick");
        if (rule == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            Map<String, Object> eventData = new HashMap<>();
            try {
                Block block = player.getLocation() != null ? player.getLocation().getBlock() : null;
                eventData.put("block_type", block != null ? block.getType().name() : null);
                Block below = block != null ? block.getRelative(0, -1, 0) : null;
                eventData.put("block_below_type", below != null ? below.getType().name() : null);
            } catch (Exception ignored) {
                eventData.put("block_type", null);
                eventData.put("block_below_type", null);
            }

            Map<String, Object> vars = ScriptVars.create()
                    .event(eventData)
                    .subject(player)
                    .build();
            applyRule(rule, player, "player_fast_tick", vars);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        ModifierRule rule = events.get("player_respawn");
        if (rule == null) return;

        Player player = event.getPlayer();
        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        applyRule(rule, player, "player_respawn", vars);
    }

    @EventHandler
    public void onPiglinBarter(PiglinBarterEvent event) {
        ModifierRule rule = events.get("piglin_barter");
        if (rule == null) return;

        Entity subject = null;
        try {
            subject = event.getEntity();
        } catch (Exception ignored) {
            subject = null;
        }

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(subject)
                .build();

        applyRule(rule, null, "piglin_barter", vars);
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ModifierRule rule = events.get("entity_potion_effect");
        if (rule == null) return;

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .subject(player)
                .build();

        boolean deny = applyRule(rule, player, "entity_potion_effect", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        ModifierRule rule = events.get("block_from_to");
        if (rule == null) return;

        Map<String, Object> vars = ScriptVars.create()
                .event(event)
                .build();

        boolean deny = applyRule(rule, null, "block_from_to", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    private boolean applyRule(ModifierRule rule, Player player, String subject, Map<String, Object> vars) {
        // subjectId: <modifier_name>:<event>
        String subjectId = getName() + ":" + subject.toLowerCase(Locale.ROOT);
        ScriptContext ctx = new ScriptContext(rdsmp, player, subjectId, ScriptPhase.MODIFIER, vars);

        boolean pass = ScriptEngine.allConditionsPass(ctx, rule.requireAll());
        if (pass) {
            ActionResult r = ScriptEngine.runAllWithResult(ctx, rule.onPass());
            return r != null && r.deny();
        }

        ActionResult r = ScriptEngine.runAllWithResult(ctx, rule.onFail());
        return rule.denyOnFail() || (r != null && r.deny());
    }
}
