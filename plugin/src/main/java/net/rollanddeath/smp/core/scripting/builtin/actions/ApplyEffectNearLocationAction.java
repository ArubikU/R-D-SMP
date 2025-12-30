package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

final class ApplyEffectNearLocationAction {
    private ApplyEffectNearLocationAction() {}

    static void register() {
        ActionRegistrar.register("apply_effect_near_location", ApplyEffectNearLocationAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Object key = raw.get("where");
        if (key == null) key = raw.get("location");
        if (key == null) key = raw.get("key");
        if (key == null) key = raw.get("location_key");
        
        String effectName = Resolvers.string(null, raw, "effect");
        if (key == null || effectName == null || effectName.isBlank()) return null;

        Integer duration = Resolvers.integer(null, raw, "duration");
        Integer amplifier = Resolvers.integer(null, raw, "amplifier");
        int dur = duration != null ? duration : 40;
        int amp = amplifier != null ? amplifier : 0;
        Integer radius = Resolvers.integer(null, raw, "radius");
        int r = radius != null ? Math.max(0, radius) : 5;
        boolean includePlayers = raw.get("include_players") instanceof Boolean b ? b : true;
        boolean includeMobs = raw.get("include_mobs") instanceof Boolean b ? b : true;
        boolean ambient = raw.get("ambient") instanceof Boolean b ? b : false;
        boolean particles = raw.get("particles") instanceof Boolean b ? b : false;

        final Object finalKey = key;

        return ctx -> {
            Location loc = Resolvers.resolveLocation(ctx, finalKey);
            if (loc == null) return ActionResult.ALLOW;

            PotionEffectType type = PotionEffectType.getByName(effectName);
            if (type == null) return ActionResult.ALLOW;

            PotionEffect effect = new PotionEffect(type, dur, amp, ambient, particles);

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity e : loc.getWorld().getNearbyEntities(loc, r, r, r)) {
                    if (!(e instanceof LivingEntity le)) continue;
                    if (e instanceof Player && !includePlayers) continue;
                    if (!(e instanceof Player) && !includeMobs) continue;
                    le.addPotionEffect(effect);
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
