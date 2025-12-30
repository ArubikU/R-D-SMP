package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class ApplyEffectToMobAction {
    private ApplyEffectToMobAction() {
    }

    static void register() {
        ActionRegistrar.register("apply_effect_to_mob", ApplyEffectToMobAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String effect = Resolvers.string(null, raw, "effect");
        if (effect == null || effect.isBlank()) return null;
        Integer duration = Resolvers.integer(null, raw, "duration");
        Integer amplifier = Resolvers.integer(null, raw, "amplifier");
        int dur = duration != null ? duration : 40;
        int amp = amplifier != null ? amplifier : 0;
        boolean ambient = raw.get("ambient") instanceof Boolean b ? b : false;
        boolean particles = raw.get("particles") instanceof Boolean b ? b : false;
        return BuiltInActions.applyEffectToMob(effect, dur, amp, ambient, particles);
    }
}
