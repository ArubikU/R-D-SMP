package net.rollanddeath.smp.core.roles;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

public class RoleManager {

    private final RollAndDeathSMP plugin;
    private final Map<RoleType, Role> roles = new HashMap<>();
    private final NamespacedKey roleKey;

    public RoleManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.roleKey = new NamespacedKey(plugin, "player_role");
    }

    public void registerRole(Role role) {
        roles.put(role.getType(), role);
        role.onEnable();
    }

    public RoleType getPlayerRole(Player player) {
        String roleName = player.getPersistentDataContainer().get(roleKey, PersistentDataType.STRING);
        if (roleName == null) return null;
        try {
            return RoleType.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void setPlayerRole(Player player, RoleType roleType) {
        if (roleType == null) {
            player.getPersistentDataContainer().remove(roleKey);
            player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Tu rol ha sido eliminado."));
        } else {
            player.getPersistentDataContainer().set(roleKey, PersistentDataType.STRING, roleType.name());
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Tu nuevo rol es: <bold>" + roleType.getName()));
            player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>" + roleType.getDescription()));
        }
    }

    public Role getRole(RoleType type) {
        return roles.get(type);
    }
}
