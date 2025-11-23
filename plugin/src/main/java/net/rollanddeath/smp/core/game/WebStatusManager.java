package net.rollanddeath.smp.core.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.LifeManager;
import net.rollanddeath.smp.core.modifiers.ModifierManager;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleManager;
import net.rollanddeath.smp.core.teams.Team;
import net.rollanddeath.smp.core.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class WebStatusManager {

    private final RollAndDeathSMP plugin;
    private final Gson gson;

    public WebStatusManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        startUpdateTask();
    }

    private void startUpdateTask() {
        // Update every 30 seconds
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::updateStatus, 600L, 600L);
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
                
                Role role = roleManager.getPlayerRole(p.getPlayer());
                playerObj.addProperty("role", role != null ? role.getName() : null);
            } else {
                // For offline players, we might not have easy access to lives/role if they are stored in PDC on the player entity
                // But LifeManager uses PDC on player. If player is offline, we can't access PDC easily without loading data.
                // For now, skip detailed info for offline players or implement offline data loading.
                // LifeManager seems to use player.getPersistentDataContainer().
                playerObj.addProperty("lives", "?");
            }
            
            players.add(playerObj);
        }
        root.add("players", players);

        File file = new File(plugin.getDataFolder(), "status.json");
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save status.json: " + e.getMessage());
        }
    }
}
