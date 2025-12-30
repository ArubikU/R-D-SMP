package net.rollanddeath.smp.core.modifiers;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Modifier implements Listener {
    
    protected final JavaPlugin plugin;
    private final String name;
    private final String type;
    private final String description;

    public Modifier(JavaPlugin plugin, String name, String type, String description) {
        this.plugin = plugin;
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    // Called when the modifier is activated for the day
    public void onEnable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // Called when the modifier is deactivated (if ever needed, though they accumulate)
    public void onDisable() {
        org.bukkit.event.HandlerList.unregisterAll(this);
    }
}
