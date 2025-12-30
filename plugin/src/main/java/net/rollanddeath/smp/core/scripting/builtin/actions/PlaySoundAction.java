package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class PlaySoundAction {
    private PlaySoundAction() {
    }

    static void register() {
        ActionRegistrar.register("play_sound", PlaySoundAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String sound = Resolvers.string(null, raw, "sound");
        Object soundsObj = raw.get("sounds");
        List<String> sounds = null;
        if (soundsObj instanceof List<?> list && !list.isEmpty()) {
            sounds = list.stream().filter(String.class::isInstance).map(String.class::cast).toList();
        }

        Double volume = Resolvers.doubleVal(null, raw, "volume");
        Double pitch = Resolvers.doubleVal(null, raw, "pitch");
        float vol = volume != null ? volume.floatValue() : 1.0f;
        float pit = pitch != null ? pitch.floatValue() : 1.0f;

        if ((sound == null || sound.isBlank()) && (sounds == null || sounds.isEmpty())) return null;
        return BuiltInActions.playSound(sound, sounds, vol, pit);
    }
}
