package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

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
        return BuiltInActions.setWeatherAllWorlds(storm, thunder, weatherDuration, thunderDuration);
    }
}
