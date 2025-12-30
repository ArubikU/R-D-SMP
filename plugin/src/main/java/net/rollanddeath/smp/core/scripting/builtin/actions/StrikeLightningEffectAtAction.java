package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;
import org.bukkit.Location;
import org.bukkit.entity.Player;

final class StrikeLightningEffectAtAction {
    private StrikeLightningEffectAtAction() {
    }

    static void register() {
        ActionRegistrar.register("strike_lightning_effect_at", StrikeLightningEffectAtAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object locSpec = firstNonNull(raw, "location", "where", "key", "location_key");
        if (locSpec == null) return null;
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            Player player = ctx.player();
            Location loc = Resolvers.location(locSpec, ctx, player != null ? player.getWorld() : null);
            if (loc == null || loc.getWorld() == null) return ActionResult.ALLOW;

            Location target = loc.clone();
            BuiltInActions.runSync(plugin, () -> {
                try {
                    target.getWorld().strikeLightningEffect(target);
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }

    private static Object firstNonNull(java.util.Map<?, ?> raw, String... keys) {
        if (raw == null || keys == null) return null;
        for (String k : keys) {
            if (k == null) continue;
            Object v = raw.get(k);
            if (v != null) return v;
        }
        return null;
    }
}