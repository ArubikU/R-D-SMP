package net.rollanddeath.smp.integration;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.ModifierManager;
import net.rollanddeath.smp.core.roles.RoleType;
import net.rollanddeath.smp.core.teams.Team;
import net.rollanddeath.smp.core.game.GameManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class PlaceholderHook extends PlaceholderExpansion {

    private final RollAndDeathSMP plugin;

    public PlaceholderHook(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "rdsmp";
    }

    @Override
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // Mantenerse registrado tras /papi reload
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String params) {
        if (offlinePlayer == null || offlinePlayer.getUniqueId() == null) {
            return "";
        }

        String key = params.toLowerCase();
        Player player = offlinePlayer.getPlayer();

        // Slots: %rdsmp_active_event_1%, %rdsmp_active_modifier_2%, etc.
        if (key.startsWith("active_event_") || key.startsWith("active_modifier_")) {
            String number = key.substring(key.lastIndexOf('_') + 1);
            int idx;
            try {
                idx = Integer.parseInt(number);
            } catch (NumberFormatException e) {
                return "";
            }
            if (idx < 1) return "";
            List<String> actives = plugin.getModifierManager().getActiveModifiers().stream().sorted().toList();
            return idx <= actives.size() ? actives.get(idx - 1) : "";
        }

        switch (key) {
            case "lives":
                if (player == null) return "";
                if (!plugin.getLifeManager().isEnabled()) return "off";
                return String.valueOf(plugin.getLifeManager().getLives(player));
            case "eliminated":
                if (player == null) return "";
                if (!plugin.getLifeManager().isEnabled()) return "off";
                return plugin.getLifeManager().isEliminated(player) ? "si" : "no";
            case "role":
                if (player == null) return "";
                RoleType role = plugin.getRoleManager().getPlayerRole(player);
                return role != null ? role.getName() : "Sin rol";
            case "team":
                Team team = plugin.getTeamManager().getTeam(offlinePlayer.getUniqueId());
                return team != null ? team.getName() : "Sin equipo";
            case "team_wars_count": {
                Team t = plugin.getTeamManager().getTeam(offlinePlayer.getUniqueId());
                return t != null ? String.valueOf(t.getActiveWars().size()) : "0";
            }
            case "team_wars": {
                Team t = plugin.getTeamManager().getTeam(offlinePlayer.getUniqueId());
                if (t == null || t.getActiveWars().isEmpty()) return "Sin guerras";
                return String.join(", ", t.getActiveWars());
            }
            case "team_friendly_fire": {
                Team t = plugin.getTeamManager().getTeam(offlinePlayer.getUniqueId());
                return t != null && t.isFriendlyFire() ? "on" : "off";
            }
            case "active_modifiers_count":
            case "active_events_count":
                return String.valueOf(plugin.getModifierManager().getActiveModifiers().size());
            case "active_modifiers":
            case "active_events": {
                Set<String> actives = plugin.getModifierManager().getActiveModifiers();
                return actives.isEmpty() ? "Ninguno" : String.join(", ", actives);
            }
            case "last_event": {
                ModifierManager mm = plugin.getModifierManager();
                List<String> history = mm.getEventHistory();
                return history.isEmpty() ? "Ninguno" : history.get(history.size() - 1);
            }
            case "last_event_1":
            case "last_event_2":
            case "last_event_3": {
                ModifierManager mm = plugin.getModifierManager();
                List<String> history = mm.getEventHistory();
                int n = key.charAt(key.length() - 1) - '0';
                if (history.isEmpty() || n < 1) return "";
                int idx = history.size() - n;
                return idx >= 0 ? history.get(idx) : "";
            }
            case "combat_remaining":
                return String.valueOf(plugin.getCombatLogManager().getRemainingCombatSeconds(offlinePlayer.getUniqueId()));
            case "combat_status": {
                int remaining = plugin.getCombatLogManager().getRemainingCombatSeconds(offlinePlayer.getUniqueId());
                return remaining > 0 ? "en_combate" : "libre";
            }
            case "combat_opponent":
                return plugin.getCombatLogManager().getCombatOpponentName(offlinePlayer.getUniqueId());
            case "next_event": {
                GameManager gm = plugin.getGameManager();
                long seconds = gm.getTimeUntilNextEvent().toSeconds();
                long h = seconds / 3600;
                long m = (seconds % 3600) / 60;
                return h + "h " + m + "m";
            }
            case "next_event_exact": {
                GameManager gm = plugin.getGameManager();
                return gm.getNextEventTime().toString();
            }
            case "daily_roll_ready": {
                return plugin.getDailyRollManager().isRollAvailable(offlinePlayer.getUniqueId()) ? "ready" : "wait";
            }
            case "daily_roll_remaining": {
                long secs = plugin.getDailyRollManager().getTimeUntilNextRoll(offlinePlayer.getUniqueId()).toSeconds();
                long h = secs / 3600;
                long m = (secs % 3600) / 60;
                return h + "h " + m + "m";
            }
            case "daily_roll_time": {
                long secs = plugin.getDailyRollManager().getTimeUntilNextRoll(offlinePlayer.getUniqueId()).toSeconds();
                return String.valueOf(secs);
            }
            case "day":
                return String.valueOf(plugin.getGameManager().getCurrentDay());
            default:
                return null;
        }
    }
}
