package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

final class ApplyRandomEffectAction {
    private ApplyRandomEffectAction() {
    }

    static void register() {
        ActionRegistrar.register("apply_random_effect", ApplyRandomEffectAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Object effectsObj = raw.get("effects");
        @SuppressWarnings("unchecked")
        List<Object> effects = effectsObj instanceof List<?> list ? list.stream().toList() : null;
        Object singleEffectSpec = Resolvers.plain(raw, "effect");
        if ((effects == null || effects.isEmpty()) && singleEffectSpec == null) return null;

        Object durationSpec = Resolvers.plain(raw, "duration");
        Object amplifierSpec = Resolvers.plain(raw, "amplifier");
        boolean ambient = raw.get("ambient") instanceof Boolean b ? b : false;
        boolean particles = raw.get("particles") instanceof Boolean b ? b : false;

        List<Object> finalList = (effects != null && !effects.isEmpty()) ? effects : List.of(singleEffectSpec);
        return ctx -> {
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            if (finalList == null || finalList.isEmpty()) return ActionResult.ALLOW;

            List<String> cleaned = finalList.stream()
                .map(spec -> Resolvers.string(ctx, spec))
                .filter(s -> s != null && !s.isBlank())
                .toList();
            if (cleaned.isEmpty()) return ActionResult.ALLOW;

            String chosen = cleaned.get(ThreadLocalRandom.current().nextInt(cleaned.size()));

            Integer duration = Resolvers.integer(ctx, durationSpec);
            Integer amplifier = Resolvers.integer(ctx, amplifierSpec);
            int dur = duration != null ? duration : 40;
            int amp = amplifier != null ? amplifier : 0;

            PotionEffectType type;
            try {
                type = PotionEffectType.getByName(chosen.trim().toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                type = null;
            }
            if (type == null) return ActionResult.ALLOW;

            PotionEffect eff = new PotionEffect(type, Math.max(1, dur), Math.max(0, amp), ambient, particles);
            BuiltInActions.runSync(ctx.plugin(), () -> player.addPotionEffect(eff));
            return ActionResult.ALLOW;
        };
    }
}
