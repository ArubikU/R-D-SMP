package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;
import org.bukkit.Location;

final class StrikeLightningAtProjectileHitAction {
    private StrikeLightningAtProjectileHitAction() {
    }

    static void register() {
        ActionRegistrar.register("strike_lightning_at_projectile_hit", StrikeLightningAtProjectileHitAction::parse, "lightning_at_projectile_hit");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;
            org.bukkit.event.entity.ProjectileHitEvent phe = ctx.nativeEvent(org.bukkit.event.entity.ProjectileHitEvent.class);
            if (phe == null) return ActionResult.ALLOW;

            BuiltInActions.runSync(plugin, () -> {
                try {
                    Location loc = null;
                    if (phe.getHitEntity() != null) {
                        loc = phe.getHitEntity().getLocation();
                    } else if (phe.getHitBlock() != null) {
                        loc = phe.getHitBlock().getLocation();
                    }
                    if (loc == null || loc.getWorld() == null) return;
                    loc.getWorld().strikeLightning(loc);
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }
}