package net.rollanddeath.smp.core.roles;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Role implements Listener {

    protected final RollAndDeathSMP plugin;
    private final RoleType type;

    public Role(RollAndDeathSMP plugin, RoleType type) {
        this.plugin = plugin;
        this.type = type;
    }

    public RoleType getType() {
        return type;
    }

    public boolean hasRole(Player player) {
        return plugin.getRoleManager().getPlayerRole(player) == type;
    }

    public void onEnable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void onDisable() {
        // Unregister listeners if needed, but usually not necessary on plugin disable
    }
}
