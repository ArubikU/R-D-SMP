package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;
import org.bukkit.Location;
import org.bukkit.entity.Player;

final class StrikeLightningAction {
    private StrikeLightningAction() {
    }

    static void register() {
        ActionRegistrar.register("strike_lightning", StrikeLightningAction::parse, "lightning");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object whereSpec = Resolvers.plain(raw, "where", "location", "at", "center");
        if (whereSpec == null) return null;
        boolean effectOnly = raw.get("effect_only") instanceof Boolean b ? b : (raw.get("effect") instanceof Boolean b2 ? b2 : false);
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            Player player = ctx.player();
            Location loc = Resolvers.location(whereSpec, ctx, player != null ? player.getWorld() : null);
            if (loc == null || loc.getWorld() == null) return ActionResult.ALLOW;

            Location target = loc.clone();
            BuiltInActions.runSync(plugin, () -> {
                try {
                    if (effectOnly) {
                        target.getWorld().strikeLightningEffect(target);
                    } else {
                        target.getWorld().strikeLightning(target);
                    }
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }
}