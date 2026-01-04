package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

final class ApplyEffectAction {
    private ApplyEffectAction() {}

    static void register() {
        ActionRegistrar.register("apply_effect", ApplyEffectAction::parse, "effect", "potion_effect", "add_effect", "apply_effect_to_event_entity");
    }

    private static Action parse(Map<?, ?> raw) {
        Object targetSpec = raw.get("target");
        if (targetSpec == null) targetSpec = raw.get("entity");
        
        Object effectSpec = Resolvers.plain(raw, "effect", "type");
        Object durationSpec = Resolvers.plain(raw, "duration", "ticks");
        Object amplifierSpec = Resolvers.plain(raw, "amplifier", "level", "strength");
        
        boolean ambient = raw.get("ambient") instanceof Boolean b ? b : false;
        boolean particles = raw.get("particles") instanceof Boolean b ? b : true;
        boolean icon = raw.get("icon") instanceof Boolean b ? b : true;
        boolean force = raw.get("force") instanceof Boolean b ? b : false;

        final Object finalTargetSpec = targetSpec;
        final Object finalEffectSpec = effectSpec;
        final Object finalDurationSpec = durationSpec;
        final Object finalAmplifierSpec = amplifierSpec;
        final boolean finalAmbient = ambient;
        final boolean finalParticles = particles;
        final boolean finalIcon = icon;
        final boolean finalForce = force;

        return ctx -> {
            List<Entity> targets = Resolvers.entities(ctx, finalTargetSpec);
            if (targets.isEmpty()) {
                // Fallback to SUBJECT if no target specified
                if (finalTargetSpec == null && ctx.subject() instanceof LivingEntity) {
                    targets = List.of(ctx.subject());
                } else {
                    return ActionResult.ALLOW;
                }
            }

            PotionEffectType type = Resolvers.potionEffectType(ctx, finalEffectSpec);
            if (type == null) return ActionResult.ALLOW;
            
            Integer duration = Resolvers.integer(ctx, finalDurationSpec);
            Integer amplifier = Resolvers.integer(ctx, finalAmplifierSpec);
            
            int dur = duration != null ? duration : 200;
            int amp = amplifier != null ? amplifier : 0;
            
            PotionEffect effect = new PotionEffect(type, dur, amp, finalAmbient, finalParticles, finalIcon);

            final List<Entity> finalTargets = targets;
            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity e : finalTargets) {
                    if (e instanceof LivingEntity le) {
                        le.addPotionEffect(effect, finalForce);
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }
}
