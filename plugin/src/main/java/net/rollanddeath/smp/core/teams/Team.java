package net.rollanddeath.smp.core.teams;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Team {
    private final String name;
    private UUID owner;
    private final Set<UUID> members;
    private boolean friendlyFire = false;
    private final Set<String> activeWars = new HashSet<>();

    public Team(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        this.members = new HashSet<>();
        this.members.add(owner);
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }
    
    public Set<String> getActiveWars() {
        return activeWars;
    }
    
    public void addWar(String teamName) {
        activeWars.add(teamName);
    }
    
    public void removeWar(String teamName) {
        activeWars.remove(teamName);
    }
    
    public boolean isAtWarWith(String teamName) {
        return activeWars.contains(teamName);
    }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public void addMember(UUID player) {
        members.add(player);
    }

    public void removeMember(UUID player) {
        members.remove(player);
    }

    public boolean isMember(UUID player) {
        return members.contains(player);
    }
}
