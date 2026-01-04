package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

final class RemoveEffectAction {
    private RemoveEffectAction() {
    }

    static void register() {
        ActionRegistrar.register("remove_effect", RemoveEffectAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object effectSpec = Resolvers.plain(raw, "effect");
        if (effectSpec == null) return null;
        Object onlyAmpSpec = Resolvers.plain(raw, "only_if_amplifier");
        return ctx -> {
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            String effect = Resolvers.string(ctx, effectSpec);
            if (effect == null || effect.isBlank()) return ActionResult.ALLOW;

            PotionEffectType type = resolvePotionEffectType(effect);
            if (type == null) return ActionResult.ALLOW;

            Integer onlyAmp = Resolvers.integer(ctx, onlyAmpSpec);

            ActionUtils.runSync(ctx.plugin(), () -> {
                PotionEffect current = player.getPotionEffect(type);
                if (current == null) return;
                if (onlyAmp != null && current.getAmplifier() != onlyAmp) return;
                player.removePotionEffect(type);
            });
            return ActionResult.ALLOW;
        };
    }

    private static PotionEffectType resolvePotionEffectType(String effectName) {
        try {
            return PotionEffectType.getByName(effectName.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (Exception e) {
            return null;
        }
    }
}
