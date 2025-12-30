package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/** Scales hunger loss when food level decreases. */
public final class MultiplyFoodLossAction {
    private MultiplyFoodLossAction() {
    }

    static void register() {
        ActionRegistrar.register("multiply_food_loss", MultiplyFoodLossAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Integer multiplier = Resolvers.integer(null, raw, "multiplier");
        if (multiplier == null || multiplier < 1) return null;
        int mult = multiplier;
        return ctx -> execute(ctx, mult);
    }

    private static ActionResult execute(ScriptContext ctx, int multiplier) {
        FoodLevelChangeEvent food = ctx.nativeEvent(FoodLevelChangeEvent.class);
        if (food == null) return ActionResult.ALLOW;

        int oldLevel = food.getEntity().getFoodLevel();
        int newLevel = food.getFoodLevel();
        if (newLevel < oldLevel) {
            int diff = oldLevel - newLevel;
            food.setFoodLevel(Math.max(0, oldLevel - (diff * multiplier)));
        }
        return ActionResult.ALLOW;
    }
}
