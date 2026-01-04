package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

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
        
        List<String> finalSounds = sounds;
        return ctx -> {
            String chosen = sound;
            if (finalSounds != null && !finalSounds.isEmpty()) {
                chosen = finalSounds.get(ThreadLocalRandom.current().nextInt(finalSounds.size()));
            }
            if (chosen == null || chosen.isBlank()) return ActionResult.ALLOW;
            
            String resolved = Resolvers.string(ctx, chosen);
            if (resolved == null || resolved.isBlank()) return ActionResult.ALLOW;
            
            Player p = ctx.player();
            Location loc = ctx.location();
            if (loc == null && p != null) loc = p.getLocation();
            
            if (loc != null) {
                // Try to parse as Sound enum, otherwise play as string
                try {
                    Sound s = Sound.valueOf(resolved.toUpperCase().replace(".", "_"));
                    if (p != null) {
                        p.playSound(loc, s, vol, pit);
                    } else {
                        loc.getWorld().playSound(loc, s, vol, pit);
                    }
                } catch (IllegalArgumentException e) {
                    if (p != null) {
                        p.playSound(loc, resolved, vol, pit);
                    } else {
                        loc.getWorld().playSound(loc, resolved, vol, pit);
                    }
                }
            }
            return ActionResult.ALLOW;
        };
    }
}
