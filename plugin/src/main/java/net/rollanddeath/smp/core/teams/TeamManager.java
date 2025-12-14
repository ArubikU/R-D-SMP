package net.rollanddeath.smp.core.teams;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TeamManager {

    private final JavaPlugin plugin;
    private final Map<String, Team> teamsByName = new HashMap<>();
    private final Map<UUID, Team> playerTeams = new HashMap<>();
    private final Map<UUID, String> pendingInvites = new HashMap<>();
    private final Set<UUID> teamChatToggles = new HashSet<>();
    private final Map<String, String> pendingAllianceRequests = new HashMap<>(); // targetTeam -> requesterTeam

    public TeamManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void declareWar(Team attacker, Team defender) {
        if (attacker.equals(defender)) return;
        
        if (!attacker.isAtWarWith(defender.getName())) {
            attacker.addWar(defender.getName());
            defender.addWar(attacker.getName()); // Mutual war
            
            Bukkit.broadcast(Component.text("⚔ ¡GUERRA! ⚔", NamedTextColor.DARK_RED));
            Bukkit.broadcast(Component.text("El equipo " + attacker.getName() + " ha declarado la guerra a " + defender.getName() + "!", NamedTextColor.RED));
            Bukkit.broadcast(Component.text("¡Sus ubicaciones serán reveladas si se acercan!", NamedTextColor.GOLD));
        }
    }

    public Team createTeam(String name, UUID owner) {
        if (teamsByName.containsKey(name)) return null;
        if (playerTeams.containsKey(owner)) return null;

        Team team = new Team(name, owner);
        teamsByName.put(name, team);
        playerTeams.put(owner, team);
        return team;
    }

    public boolean addMember(String teamName, UUID player) {
        Team team = teamsByName.get(teamName);
        if (team == null) return false;
        if (playerTeams.containsKey(player)) return false;
        if (team.getMembers().size() >= 5) return false; // Max 5 members

        team.addMember(player);
        playerTeams.put(player, team);
        return true;
    }

    public void removeMember(UUID player) {
        Team team = playerTeams.get(player);
        if (team != null) {
            team.removeMember(player);
            playerTeams.remove(player);
            
            if (team.getMembers().isEmpty()) {
                cleanupTeamReferences(team.getName());
                teamsByName.remove(team.getName());
            } else if (team.getOwner().equals(player)) {
                // Promote new owner
                UUID newOwner = team.getMembers().iterator().next();
                team.setOwner(newOwner);
            }
        }
    }

    public void invitePlayer(UUID target, String teamName) {
        pendingInvites.put(target, teamName);
    }

    public String getInvite(UUID target) {
        return pendingInvites.get(target);
    }

    public void clearInvite(UUID target) {
        pendingInvites.remove(target);
    }

    public void requestAlliance(String requesterTeam, String targetTeam) {
        pendingAllianceRequests.put(targetTeam, requesterTeam);
    }

    public String getAllianceRequester(String targetTeam) {
        return pendingAllianceRequests.get(targetTeam);
    }

    public void clearAllianceRequest(String targetTeam) {
        pendingAllianceRequests.remove(targetTeam);
    }

    public void clearAllianceRequestsInvolving(String teamName) {
        pendingAllianceRequests.entrySet().removeIf(entry ->
            entry.getKey().equalsIgnoreCase(teamName) || entry.getValue().equalsIgnoreCase(teamName)
        );
    }

    public void disbandTeam(String teamName) {
        Team team = teamsByName.get(teamName);
        if (team != null) {
            for (UUID member : team.getMembers()) {
                playerTeams.remove(member);
            }
            cleanupTeamReferences(teamName);
            teamsByName.remove(teamName);
        }
    }

    public Team getTeam(UUID player) {
        return playerTeams.get(player);
    }

    public Team getTeam(String name) {
        return teamsByName.get(name);
    }
    
    public Collection<String> getTeamNames() {
        return new HashSet<>(teamsByName.keySet());
    }
    
    public boolean areAllMembersOffline(Team team) {
        for (UUID memberId : team.getMembers()) {
            if (plugin.getServer().getPlayer(memberId) != null) {
                return false;
            }
        }
        return true;
    }

    public void loadFromConfig(ConfigurationSection section) {
        teamsByName.clear();
        playerTeams.clear();
        pendingInvites.clear();
        pendingAllianceRequests.clear();

        if (section == null) return;

        for (String name : section.getKeys(false)) {
            ConfigurationSection teamSec = section.getConfigurationSection(name);
            if (teamSec == null) continue;

            String ownerStr = teamSec.getString("owner");
            if (ownerStr == null) continue;

            UUID owner;
            try {
                owner = UUID.fromString(ownerStr);
            } catch (IllegalArgumentException ex) {
                continue;
            }

            Team team = new Team(name, owner);
            team.setFriendlyFire(teamSec.getBoolean("friendly_fire", false));

            String colorName = teamSec.getString("color");
            if (colorName != null) {
                NamedTextColor parsed = NamedTextColor.NAMES.value(colorName);
                if (parsed != null) {
                    team.setColor(parsed);
                }
            }

            for (String memberStr : teamSec.getStringList("members")) {
                try {
                    team.addMember(UUID.fromString(memberStr));
                } catch (IllegalArgumentException ignored) {
                }
            }

            for (String war : teamSec.getStringList("wars")) {
                team.addWar(war);
            }

            for (String ally : teamSec.getStringList("alliances")) {
                if (!ally.equalsIgnoreCase(name)) {
                    team.addAlliance(ally);
                }
            }

            ConfigurationSection homeSec = teamSec.getConfigurationSection("home");
            if (homeSec != null) {
                String worldName = homeSec.getString("world");
                World world = worldName != null ? plugin.getServer().getWorld(worldName) : null;
                if (world != null) {
                    double x = homeSec.getDouble("x");
                    double y = homeSec.getDouble("y");
                    double z = homeSec.getDouble("z");
                    float yaw = (float) homeSec.getDouble("yaw", 0.0);
                    float pitch = (float) homeSec.getDouble("pitch", 0.0);
                    team.setHome(new Location(world, x, y, z, yaw, pitch));
                }
            }

            // Ensure owner is recorded as member
            team.addMember(owner);

            teamsByName.put(name, team);
            for (UUID member : team.getMembers()) {
                playerTeams.put(member, team);
            }
        }

        // Ensure alliances are mutual and reference existing teams
        for (Team team : teamsByName.values()) {
            team.getAlliances().removeIf(allyName -> !teamsByName.containsKey(allyName));
        }
        for (Team team : teamsByName.values()) {
            for (String allyName : new HashSet<>(team.getAlliances())) {
                Team ally = teamsByName.get(allyName);
                if (ally != null && !ally.isAlliedWith(team.getName())) {
                    ally.addAlliance(team.getName());
                }
            }
        }
    }

    public void saveToConfig(ConfigurationSection section) {
        if (section == null) return;

        for (String key : new HashSet<>(section.getKeys(false))) {
            section.set(key, null);
        }

        for (Team team : teamsByName.values()) {
            ConfigurationSection teamSec = section.createSection(team.getName());
            teamSec.set("owner", team.getOwner().toString());
            teamSec.set("members", team.getMembers().stream().map(UUID::toString).collect(Collectors.toList()));
            teamSec.set("friendly_fire", team.isFriendlyFire());
            teamSec.set("wars", new ArrayList<>(team.getActiveWars()));
            teamSec.set("alliances", new ArrayList<>(team.getAlliances()));

            String colorKey = NamedTextColor.NAMES.key(team.getColor());
            teamSec.set("color", colorKey);

            Location home = team.getHome();
            if (home != null && home.getWorld() != null) {
                ConfigurationSection homeSec = teamSec.createSection("home");
                homeSec.set("world", home.getWorld().getName());
                homeSec.set("x", home.getX());
                homeSec.set("y", home.getY());
                homeSec.set("z", home.getZ());
                homeSec.set("yaw", home.getYaw());
                homeSec.set("pitch", home.getPitch());
            }
        }
    }

    public boolean toggleTeamChat(UUID playerId) {
        if (teamChatToggles.contains(playerId)) {
            teamChatToggles.remove(playerId);
            return false;
        }
        teamChatToggles.add(playerId);
        return true;
    }

    public boolean isTeamChatToggled(UUID playerId) {
        return teamChatToggles.contains(playerId);
    }

    public void sendTeamMessage(Player sender, String message) {
        Team team = getTeam(sender.getUniqueId());
        if (team == null) {
            sender.sendMessage(Component.text("No estás en un equipo.", NamedTextColor.RED));
            return;
        }

        Component chatFormat = Component.text("[TeamChat] ", NamedTextColor.AQUA)
                .append(Component.text(sender.getName() + ": ", NamedTextColor.WHITE))
                .append(Component.text(message, NamedTextColor.GRAY));

        for (UUID memberId : team.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                member.sendMessage(chatFormat);
            }
        }
    }

    private void cleanupTeamReferences(String teamName) {
        for (Team team : teamsByName.values()) {
            team.removeAlliance(teamName);
            team.removeWar(teamName);
        }
        clearAllianceRequestsInvolving(teamName);
    }
}
