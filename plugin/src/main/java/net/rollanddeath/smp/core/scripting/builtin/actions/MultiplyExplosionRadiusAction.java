package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.event.entity.ExplosionPrimeEvent;

final class MultiplyExplosionRadiusAction {
    private MultiplyExplosionRadiusAction() {}

    static void register() {
        ActionRegistrar.register("multiply_explosion_radius", MultiplyExplosionRadiusAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Double mult = Resolvers.doubleVal(null, raw, "multiplier");
        if (mult == null) return null;

        return ctx -> {
            ExplosionPrimeEvent epe = ctx.nativeEvent(ExplosionPrimeEvent.class);
            if (epe == null) return ActionResult.ALLOW;
            
            epe.setRadius(epe.getRadius() * mult.floatValue());
            return ActionResult.ALLOW;
        };
    }
}
