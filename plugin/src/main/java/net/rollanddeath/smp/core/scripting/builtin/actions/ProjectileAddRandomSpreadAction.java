package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.concurrent.ThreadLocalRandom;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

final class ProjectileAddRandomSpreadAction {
    private ProjectileAddRandomSpreadAction() {
    }

    static void register() {
        ActionRegistrar.register("projectile_add_random_spread", ProjectileAddRandomSpreadAction::parse, "bow_projectile_spread");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object spreadSpec = Resolvers.plain(raw, "spread", "amount");
        if (spreadSpec == null) return null;
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;
            org.bukkit.event.entity.EntityShootBowEvent bow = ctx.nativeEvent(org.bukkit.event.entity.EntityShootBowEvent.class);
            if (bow == null) return ActionResult.ALLOW;
            if (!(bow.getProjectile() instanceof Projectile proj)) return ActionResult.ALLOW;

            Double spread = Resolvers.doubleVal(ctx, spreadSpec);
            double spreadFinal = spread != null ? Math.max(0.0, spread) : 0.0;
            if (spreadFinal <= 0.0) return ActionResult.ALLOW;

            ActionUtils.runSync(plugin, () -> {
                try {
                    Vector velocity = proj.getVelocity();
                    double dx = (ThreadLocalRandom.current().nextDouble() - 0.5) * spreadFinal;
                    double dy = (ThreadLocalRandom.current().nextDouble() - 0.5) * spreadFinal;
                    double dz = (ThreadLocalRandom.current().nextDouble() - 0.5) * spreadFinal;
                    velocity.add(new Vector(dx, dy, dz));
                    proj.setVelocity(velocity);
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }
}