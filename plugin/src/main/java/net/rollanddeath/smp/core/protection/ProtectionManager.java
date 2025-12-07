package net.rollanddeath.smp.core.protection;

import net.rollanddeath.smp.core.teams.Team;
import net.rollanddeath.smp.core.teams.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class ProtectionManager {

    private final JavaPlugin plugin;
    private final TeamManager teamManager;
    private final NamespacedKey ownerKey;
    private boolean isPurgeActive = false;

    public ProtectionManager(JavaPlugin plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.ownerKey = new NamespacedKey(plugin, "block_owner");
    }

    public void setPurgeActive(boolean active) {
        this.isPurgeActive = active;
    }

    public boolean isPurgeActive() {
        return isPurgeActive;
    }

    public boolean isProtected(Block block) {
        if (isPurgeActive) return false;
        if (!(block.getState() instanceof TileState)) return false;

        String owner = getOwnerUUID(block);
        if (owner == null) return false;

        if (isOrphanedOwner(owner)) {
            clearProtection(block);
            return false;
        }

        return true;
    }

    public boolean canAccess(Player player, Block block) {
        if (isPurgeActive) return true;
        if (!isProtected(block)) return true;

        String ownerUUIDString = getOwnerUUID(block);
        if (ownerUUIDString == null) return true;
        
        UUID ownerUUID = UUID.fromString(ownerUUIDString);
        
        // 1. Check if player is owner
        if (player.getUniqueId().equals(ownerUUID)) return true;
        
        // 2. Check if player is admin bypass
        if (player.hasPermission("rd.admin.bypass")) return true;

        // 3. Check if player is in the same team as owner
        Team ownerTeam = teamManager.getTeam(ownerUUID);
        if (ownerTeam != null && ownerTeam.isMember(player.getUniqueId())) {
            return true;
        }

        return false;
    }

    public boolean isOwnerOnline(Block block) {
        String ownerUUIDString = getOwnerUUID(block);
        if (ownerUUIDString == null) return false;
        
        UUID ownerUUID = UUID.fromString(ownerUUIDString);
        
        // Check if owner is online
        if (Bukkit.getPlayer(ownerUUID) != null) return true;

        // Check if any team member is online
        Team ownerTeam = teamManager.getTeam(ownerUUID);
        if (ownerTeam != null) {
            return !teamManager.areAllMembersOffline(ownerTeam);
        }

        return false;
    }

    public void protectBlock(Player player, Block block) {
        if (!(block.getState() instanceof TileState)) return;
        applyProtection(block, player.getUniqueId());

        // Handle Double Chests
        Block otherHalf = getOtherHalf(block);
        if (otherHalf != null) {
            applyProtection(otherHalf, player.getUniqueId());
        }
    }

    private void applyProtection(Block block, UUID ownerId) {
        TileState state = (TileState) block.getState();
        PersistentDataContainer container = state.getPersistentDataContainer();
        container.set(ownerKey, PersistentDataType.STRING, ownerId.toString());
        state.update();
    }

    private String getOwnerUUID(Block block) {
        if (block.getState() instanceof TileState) {
            TileState state = (TileState) block.getState();
            if (state.getPersistentDataContainer().has(ownerKey, PersistentDataType.STRING)) {
                return state.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING);
            }
        }
        
        Block otherHalf = getOtherHalf(block);
        if (otherHalf != null && otherHalf.getState() instanceof TileState) {
            TileState otherState = (TileState) otherHalf.getState();
            if (otherState.getPersistentDataContainer().has(ownerKey, PersistentDataType.STRING)) {
                return otherState.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING);
            }
        }
        return null;
    }

    private boolean isOrphanedOwner(String ownerUUIDString) {
        try {
            UUID ownerUUID = UUID.fromString(ownerUUIDString);
            return teamManager.getTeam(ownerUUID) == null;
        } catch (IllegalArgumentException ex) {
            return true;
        }
    }

    private void clearProtection(Block block) {
        if (!(block.getState() instanceof TileState)) return;

        TileState state = (TileState) block.getState();
        PersistentDataContainer container = state.getPersistentDataContainer();
        if (container.has(ownerKey, PersistentDataType.STRING)) {
            container.remove(ownerKey);
            state.update();
        }

        Block otherHalf = getOtherHalf(block);
        if (otherHalf != null && otherHalf.getState() instanceof TileState) {
            TileState otherState = (TileState) otherHalf.getState();
            PersistentDataContainer otherContainer = otherState.getPersistentDataContainer();
            if (otherContainer.has(ownerKey, PersistentDataType.STRING)) {
                otherContainer.remove(ownerKey);
                otherState.update();
            }
        }
    }

    private Block getOtherHalf(Block block) {
        if (!(block.getState() instanceof Chest)) return null;
        Chest chest = (Chest) block.getState();
        Inventory inventory = chest.getInventory();
        
        if (inventory instanceof org.bukkit.inventory.DoubleChestInventory) {
            DoubleChest doubleChest = (DoubleChest) inventory.getHolder();
            InventoryHolder left = doubleChest.getLeftSide();
            InventoryHolder right = doubleChest.getRightSide();
            
            if (left instanceof Chest && right instanceof Chest) {
                Chest leftChest = (Chest) left;
                Chest rightChest = (Chest) right;
                
                if (leftChest.getBlock().equals(block)) {
                    return rightChest.getBlock();
                } else {
                    return leftChest.getBlock();
                }
            }
        }
        return null;
    }
}
