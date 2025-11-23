package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class EternalStormModifier extends Modifier {

    public EternalStormModifier(JavaPlugin plugin) {
        super(plugin, "Tormenta Eterna", ModifierType.CURSE, "Llueve y hay truenos constantemente.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        plugin.getServer().getWorlds().forEach(world -> {
            world.setStorm(true);
            world.setThundering(true);
            world.setWeatherDuration(Integer.MAX_VALUE);
            world.setThunderDuration(Integer.MAX_VALUE);
        });
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        plugin.getServer().getWorlds().forEach(world -> {
            world.setStorm(false);
            world.setThundering(false);
        });
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (!event.toWeatherState()) { // If trying to clear weather
            event.setCancelled(true);
        }
    }
}
