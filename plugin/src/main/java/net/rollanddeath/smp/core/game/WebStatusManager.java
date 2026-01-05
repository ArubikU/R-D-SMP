package net.rollanddeath.smp.core.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.LifeManager;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.DailyRollManager;
import net.rollanddeath.smp.core.modifiers.ModifierManager;
import net.rollanddeath.smp.core.roles.RoleManager;
import net.rollanddeath.smp.core.roles.RoleType;
import net.rollanddeath.smp.core.rules.DayRule;
import net.rollanddeath.smp.core.rules.DayRuleManager;
import net.rollanddeath.smp.core.teams.Team;
import net.rollanddeath.smp.core.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebStatusManager {

    private final RollAndDeathSMP plugin;
    private final Gson gson;
    private HttpServer server;
    private String cachedStatusJson = "{}";
    private String cachedRecipesJson = "{}";
    private String cachedDropsJson = "{}";
    private ExecutorService executor;

    public WebStatusManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        if (plugin.getConfig().getBoolean("web-status.enabled", true)) {
            int port = plugin.getConfig().getInt("web-status.port", 8081);
            String host = plugin.getConfig().getString("web-status.host", "0.0.0.0");
            try {
                server = HttpServer.create(new InetSocketAddress(host, port), 0);
                server.createContext("/status", exchange -> sendJsonResponse(exchange, cachedStatusJson));
                server.createContext("/recipes", exchange -> sendJsonResponse(exchange, cachedRecipesJson));
                server.createContext("/drops", exchange -> sendJsonResponse(exchange, cachedDropsJson));

                int threads = Math.max(2, plugin.getConfig().getInt("web-status.threads", 8));
                executor = Executors.newFixedThreadPool(threads);
                server.setExecutor(executor);
                server.start();
                plugin.getLogger().info("Web Status Server started on " + host + ":" + port);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to start Web Status Server: " + e.getMessage());
            }
        }

        // Warm up caches so the first request has data.
        try {
            updateStatus();
            updateRecipesCache();
            updateDropsCache();
        } catch (Exception e) {
            plugin.getLogger().warning("Initial web cache build failed: " + e.getMessage());
        }

        startUpdateTask();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    private void startUpdateTask() {
        // Update every ~2 seconds for faster web sync
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            updateStatus();
            updateRecipesCache();
            updateDropsCache();
        }, 20L, 40L);
    }

    public void updateStatus() {
        GameManager gameManager = plugin.getGameManager();
        ModifierManager modifierManager = plugin.getModifierManager();
        LifeManager lifeManager = plugin.getLifeManager();
        TeamManager teamManager = plugin.getTeamManager();
        RoleManager roleManager = plugin.getRoleManager();
        DailyRollManager dailyRollManager = plugin.getDailyRollManager();
        var mobRotation = plugin.getDailyMobRotationManager();
        DayRuleManager dayRuleManager = plugin.getDayRuleManager();

        boolean livesEnabled = lifeManager != null && lifeManager.isEnabled();

        JsonObject root = new JsonObject();
        root.addProperty("day", gameManager.getCurrentDay());
        root.addProperty("permadeath", gameManager.isPermadeathActive());
        root.addProperty("permadeath_day", gameManager.getPermadeathDay());
        root.addProperty("nether_unlocked", gameManager.isNetherUnlocked());
        root.addProperty("nether_unlock_day", gameManager.getNetherUnlockDay());
        root.addProperty("end_unlocked", gameManager.isEndUnlocked());
        root.addProperty("end_unlock_day", gameManager.getEndUnlockDay());
        root.addProperty("lives_enabled", livesEnabled);

        Instant nextEvent = gameManager.getNextEventTime();
        Duration untilNext = gameManager.getTimeUntilNextEvent();
        root.addProperty("next_event_time", nextEvent.toEpochMilli());
        root.addProperty("next_event_seconds", untilNext.isNegative() ? 0L : untilNext.getSeconds());

        JsonObject rollOdds = new JsonObject();
        rollOdds.addProperty("common", 65);
        rollOdds.addProperty("rare", 20);
        rollOdds.addProperty("epic", 12);
        rollOdds.addProperty("legendary", 3);
        root.add("daily_roll_odds", rollOdds);

        JsonArray modifiers = new JsonArray();
        for (String mod : modifierManager.getActiveModifiers()) {
            modifiers.add(mod);
        }
        root.add("active_modifiers", modifiers);

        JsonArray activeDayRules = new JsonArray();
        if (dayRuleManager != null) {
            for (DayRule rule : dayRuleManager.getRulesUpTo(gameManager.getCurrentDay())) {
                JsonObject obj = new JsonObject();
                obj.addProperty("day", rule.getDay());
                obj.addProperty("name", rule.getName());
                obj.addProperty("description", rule.getDescription());
                activeDayRules.add(obj);
            }
        }
        root.add("active_day_rules", activeDayRules);

        JsonArray activeMobs = new JsonArray();
        if (mobRotation != null) {
            for (String mobId : mobRotation.getActiveMobs()) {
                net.rollanddeath.smp.core.mobs.CustomMob mob = plugin.getMobManager().getMob(mobId);
                if (mob == null) continue;

                JsonObject mobObj = new JsonObject();
                mobObj.addProperty("id", mob.getId());
                mobObj.addProperty("name", mob.getDisplayName());
                activeMobs.add(mobObj);
            }
            root.addProperty("active_mob_count", mobRotation.getActiveMobs().size());
            root.addProperty("last_mob_day", mobRotation.getLastDay());
        }
        root.add("active_mobs", activeMobs);

        JsonArray history = new JsonArray();
        for (String mod : modifierManager.getEventHistory()) {
            history.add(mod);
        }
        root.add("event_history", history);

        JsonArray players = new JsonArray();
        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            if (p.getName() == null) {
                continue;
            }
            JsonObject playerObj = new JsonObject();
            playerObj.addProperty("name", p.getName());
            playerObj.addProperty("uuid", p.getUniqueId().toString());
            playerObj.addProperty("online", p.isOnline());

            if (p.isOnline()) {
                Player online = p.getPlayer();
                if (online != null) {
                    int lives = lifeManager != null ? lifeManager.getLives(online) : -1;
                    playerObj.addProperty("lives", lives);
                    playerObj.addProperty("lives_remaining", lives);

                    double health = online.getHealth();
                    playerObj.addProperty("health", health);
                    AttributeInstance maxHealthAttr = online.getAttribute(Attribute.MAX_HEALTH);
                    double maxHealth = maxHealthAttr != null ? maxHealthAttr.getValue() : online.getMaxHealth();
                    playerObj.addProperty("max_health", maxHealth);

                    Team team = teamManager != null ? teamManager.getTeam(p.getUniqueId()) : null;
                    playerObj.addProperty("team", team != null ? team.getName() : null);

                    RoleType role = roleManager != null ? roleManager.getPlayerRole(online) : null;
                    playerObj.addProperty("role", role != null ? role.getName() : null);
                }
            } else {
                int storedLives = livesEnabled && lifeManager != null ? lifeManager.getStoredLives(p.getUniqueId()) : -1;
                playerObj.addProperty("lives", storedLives);
                playerObj.addProperty("lives_remaining", storedLives);
            }

            Duration rollRemaining = dailyRollManager != null
                ? dailyRollManager.getTimeUntilNextRoll(p.getUniqueId())
                : Duration.ZERO;
            playerObj.addProperty("daily_roll_available", rollRemaining.isZero());

            players.add(playerObj);
        }
        root.add("players", players);

        this.cachedStatusJson = gson.toJson(root);

        // Also save to file for backup/legacy
        File file = new File(plugin.getDataFolder(), "status.json");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(this.cachedStatusJson);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save status.json: " + e.getMessage());
        }
    }

    /**
     * Exposes registered custom items (including drop-only items) for the front-end.
     */
    public void updateDropsCache() {
        try {
            File file = ensureMobsFile();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

            JsonObject root = new JsonObject();
            root.addProperty("generated_at", Instant.now().toEpochMilli());
            root.addProperty("source", "mobs.yml");

            JsonArray drops = new JsonArray();
            ConfigurationSection mobsSection = yaml.getConfigurationSection("mobs");
            if (mobsSection != null) {
                for (String mobId : mobsSection.getKeys(false)) {
                    ConfigurationSection mobSection = mobsSection.getConfigurationSection(mobId);
                    if (mobSection == null) continue;

                    String mobName = mobSection.getString("name", mobId);
                    List<?> loot = mobSection.getList("loot");
                    if (loot == null) continue;

                    for (Object entry : loot) {
                        if (!(entry instanceof Map<?, ?> map)) continue;

                        String customId = asString(map.get("custom_item"));
                        String material = asString(map.get("material"));
                        double chance = asDouble(map.get("chance"), 0.0);
                        int amount = asInt(map.get("amount"), 1);

                        JsonObject obj = new JsonObject();
                        obj.addProperty("mob_id", mobId);
                        obj.addProperty("mob_name", mobName);
                        obj.addProperty("chance", chance);
                        obj.addProperty("amount", amount);

                        if (customId != null && !customId.isBlank()) {
                            obj.addProperty("item_type", "custom");
                            obj.addProperty("item_id", customId);

                            CustomItem custom = plugin.getItemManager().getItem(customId.trim().toUpperCase(Locale.ROOT));
                            if (custom != null) {
                                obj.addProperty("item_display_name", custom.getDisplayName());
                                ItemStack stack = custom.getItemStack();
                                if (stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData()) {
                                    obj.addProperty("custom_model_data", stack.getItemMeta().getCustomModelData());
                                }

                                RoleType role = custom.getRequiredRoleType();
                                if (role != null) obj.addProperty("required_role", role.getName());

                                obj.addProperty("material", stack.getType().name());
                            }
                        } else if (material != null && !material.isBlank()) {
                            obj.addProperty("item_type", "vanilla");
                            obj.addProperty("item_id", material);
                            obj.addProperty("item_display_name", material);
                        }

                        drops.add(obj);
                    }
                }
            }

            root.addProperty("count", drops.size());
            root.add("drops", drops);

            this.cachedDropsJson = gson.toJson(root);

            File out = new File(plugin.getDataFolder(), "drops.json");
            try (FileWriter writer = new FileWriter(out)) {
                writer.write(this.cachedDropsJson);
            } catch (IOException e) {
                plugin.getLogger().warning("Could not save drops.json: " + e.getMessage());
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Could not build drops.json: " + e.getMessage());
        }
    }

    private File ensureMobsFile() {
        if (!plugin.getDataFolder().exists()) {
            //noinspection ResultOfMethodCallIgnored
            plugin.getDataFolder().mkdirs();
        }
        File file = new File(plugin.getDataFolder(), "mobs.yml");
        if (!file.exists()) {
            plugin.saveResource("mobs.yml", false);
        }
        return file;
    }

    private String asString(Object o) {
        return o == null ? null : Objects.toString(o, null);
    }

    private double asDouble(Object o, double fallback) {
        if (o instanceof Number n) return n.doubleValue();
        try {
            return o != null ? Double.parseDouble(o.toString()) : fallback;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private int asInt(Object o, int fallback) {
        if (o instanceof Number n) return n.intValue();
        try {
            return o != null ? Integer.parseInt(o.toString()) : fallback;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /**
     * Exposes the raw recipes.yml content (plus a few enriched fields) for the front-end.
     */
    public void updateRecipesCache() {
        try {
            File file = ensureRecipesFile();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

            JsonObject root = new JsonObject();
            root.addProperty("generated_at", Instant.now().toEpochMilli());
            root.addProperty("source", "recipes.yml");

            ConfigurationSection recipesSection = yaml.getConfigurationSection("recipes");
            JsonArray recipes = new JsonArray();
            if (recipesSection != null) {
                for (String id : recipesSection.getKeys(false)) {
                    ConfigurationSection section = recipesSection.getConfigurationSection(id);
                    if (section == null) continue;
                    recipes.add(buildRecipeJson(id, section));
                }
            }

            root.addProperty("count", recipes.size());
            root.add("recipes", recipes);

            this.cachedRecipesJson = gson.toJson(root);
        } catch (Exception e) {
            plugin.getLogger().warning("Could not build recipes.json: " + e.getMessage());
        }
    }

    private JsonObject buildRecipeJson(String id, ConfigurationSection section) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", id);
        obj.addProperty("type", section.getString("type", "unknown"));
        obj.add("definition", toJsonValue(section.getValues(false)));

        ConfigurationSection resultSection = section.getConfigurationSection("result");
        if (resultSection != null) {
            String customId = resultSection.getString("custom");
            if (customId != null && !customId.isBlank()) {
                CustomItem custom = plugin.getItemManager().getItem(customId.trim().toUpperCase(Locale.ROOT));
                if (custom != null) {
                    JsonObject meta = new JsonObject();
                    meta.addProperty("custom_id", custom.getId());
                    meta.addProperty("display_name", custom.getDisplayName());

                    ItemStack stack = custom.getItemStack();
                    if (stack.getItemMeta() != null && stack.getItemMeta().hasCustomModelData()) {
                        meta.addProperty("custom_model_data", stack.getItemMeta().getCustomModelData());
                    }

                    RoleType role = custom.getRequiredRoleType();
                    if (role != null) {
                        meta.addProperty("required_role", role.getName());
                    }
                    obj.add("result_meta", meta);
                }
            }
        }

        // Optional rules already live inside definition; expose a shortcut too.
        ConfigurationSection rules = section.getConfigurationSection("rules");
        if (rules != null) {
            obj.add("rules", toJsonValue(rules));
        }

        return obj;
    }

    private File ensureRecipesFile() {
        if (!plugin.getDataFolder().exists()) {
            //noinspection ResultOfMethodCallIgnored
            plugin.getDataFolder().mkdirs();
        }
        File file = new File(plugin.getDataFolder(), "recipes.yml");
        if (!file.exists()) {
            plugin.saveResource("recipes.yml", false);
        }
        return file;
    }

    private JsonElement toJsonValue(Object value) {
        if (value == null) return JsonNull.INSTANCE;

        if (value instanceof ConfigurationSection section) {
            JsonObject obj = new JsonObject();
            for (String key : section.getKeys(false)) {
                obj.add(key, toJsonValue(section.get(key)));
            }
            return obj;
        }

        if (value instanceof Map<?, ?> map) {
            JsonObject obj = new JsonObject();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() instanceof String k) {
                    obj.add(k, toJsonValue(entry.getValue()));
                }
            }
            return obj;
        }

        if (value instanceof List<?> list) {
            JsonArray arr = new JsonArray();
            for (Object v : list) {
                arr.add(toJsonValue(v));
            }
            return arr;
        }

        if (value instanceof Number n) return new JsonPrimitive(n);
        if (value instanceof Boolean b) return new JsonPrimitive(b);
        if (value instanceof Character c) return new JsonPrimitive(c);
        if (value instanceof String s) return new JsonPrimitive(s);

        return new JsonPrimitive(String.valueOf(value));
    }

    private void sendJsonResponse(HttpExchange exchange, String body) throws IOException {
        byte[] response = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        } finally {
            exchange.close();
        }
    }
}