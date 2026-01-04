package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.Bukkit;
import org.bukkit.World;

final class SetWeatherAllWorldsAction {
    private SetWeatherAllWorldsAction() {
    }

    static void register() {
        ActionRegistrar.register("set_weather_all_worlds", SetWeatherAllWorldsAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Boolean storm = raw.get("storm") instanceof Boolean b ? b : null;
        Boolean thunder = raw.get("thunder") instanceof Boolean b ? b : null;
        Integer weatherDuration = Resolvers.integer(null, raw, "weather_duration");
        Integer thunderDuration = Resolvers.integer(null, raw, "thunder_duration");
        if (storm == null && thunder == null) return null;
        
        return ctx -> {
            for (World w : Bukkit.getWorlds()) {
                if (storm != null) w.setStorm(storm);
                if (thunder != null) w.setThundering(thunder);
                if (weatherDuration != null) w.setWeatherDuration(weatherDuration);
                if (thunderDuration != null) w.setThunderDuration(thunderDuration);
            }
            return ActionResult.ALLOW;
        };
    }
}
