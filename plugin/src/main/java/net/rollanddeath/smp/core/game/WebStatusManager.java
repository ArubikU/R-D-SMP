package net.rollanddeath.smp.core.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpServer;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.LifeManager;
import net.rollanddeath.smp.core.modifiers.ModifierManager;
import net.rollanddeath.smp.core.roles.RoleManager;
import net.rollanddeath.smp.core.roles.RoleType;
import net.rollanddeath.smp.core.teams.Team;
import net.rollanddeath.smp.core.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class WebStatusManager {

    private final RollAndDeathSMP plugin;
    private final Gson gson;
    private HttpServer server;
    private String cachedJson = "{}";

    public WebStatusManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        
        if (plugin.getConfig().getBoolean("web-status.enabled", true)) {
            int port = plugin.getConfig().getInt("web-status.port", 8081);
            try {
                server = HttpServer.create(new InetSocketAddress(port), 0);
                server.createContext("/status", exchange -> {
                    byte[] response = cachedJson.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().add("Content-Type", "application/json");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.sendResponseHeaders(200, response.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response);
                    }
                });
                server.setExecutor(null);
                server.start();
                plugin.getLogger().info("Web Status Server started on port " + port);
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to start Web Status Server: " + e.getMessage());
            }
        }

        startUpdateTask();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private void startUpdateTask() {
        // Update every 30 seconds
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::updateStatus, 20L, 600L);
    }

    public void updateStatus() {
        GameManager gameManager = plugin.getGameManager();
        ModifierManager modifierManager = plugin.getModifierManager();
        LifeManager lifeManager = plugin.getLifeManager();
        TeamManager teamManager = plugin.getTeamManager();
        RoleManager roleManager = plugin.getRoleManager();

        JsonObject root = new JsonObject();
        root.addProperty("day", gameManager.getCurrentDay());
        root.addProperty("permadeath", gameManager.isPermadeathActive());

        JsonArray modifiers = new JsonArray();
        for (String mod : modifierManager.getActiveModifiers()) {
            modifiers.add(mod);
        }
        root.add("active_modifiers", modifiers);

        JsonArray players = new JsonArray();
        for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
            JsonObject playerObj = new JsonObject();
            playerObj.addProperty("name", p.getName());
            playerObj.addProperty("uuid", p.getUniqueId().toString());
            playerObj.addProperty("online", p.isOnline());
            
            if (p.isOnline()) {
                playerObj.addProperty("lives", lifeManager.getLives(p.getPlayer()));
                
                Team team = teamManager.getTeam(p.getUniqueId());
                playerObj.addProperty("team", team != null ? team.getName() : null);
                
                RoleType role = roleManager.getPlayerRole(p.getPlayer());
                playerObj.addProperty("role", role != null ? role.getName() : null);
            } else {
                playerObj.addProperty("lives", "?");
            }
            
            players.add(playerObj);
        }
        root.add("players", players);

        this.cachedJson = gson.toJson(root);

        // Also save to file for backup/legacy
        File file = new File(plugin.getDataFolder(), "status.json");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(this.cachedJson);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save status.json: " + e.getMessage());
        }
    }
}
