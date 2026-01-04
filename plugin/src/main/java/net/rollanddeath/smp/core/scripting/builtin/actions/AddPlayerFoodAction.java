package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.entity.Player;

final class AddPlayerFoodAction {
    private AddPlayerFoodAction() {
    }

    static void register() {
        ActionRegistrar.register("add_player_food", AddPlayerFoodAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object foodSpec = Resolvers.plain(raw, "food");
        Object saturationSpec = Resolvers.plain(raw, "saturation");
        if (foodSpec == null && saturationSpec == null) return null;
        return ctx -> execute(ctx, foodSpec, saturationSpec);
    }

    private static ActionResult execute(ScriptContext ctx, Object foodSpec, Object saturationSpec) {
        RollAndDeathSMP plugin = ctx.plugin();
        Player player = ctx.player();
        if (player == null) return ActionResult.ALLOW;

        Double foodDelta = Resolvers.doubleVal(ctx, foodSpec);
        Double saturationDelta = Resolvers.doubleVal(ctx, saturationSpec);
        double fd = foodDelta != null ? foodDelta : 0.0;
        double sd = saturationDelta != null ? saturationDelta : 0.0;

        ActionUtils.runSync(plugin, () -> {
            try {
                int food = player.getFoodLevel();
                float sat = player.getSaturation();

                int newFood = (int) Math.round(food + fd);
                newFood = Math.max(0, Math.min(20, newFood));
                float newSat = (float) (sat + sd);
                newSat = Math.max(0.0f, Math.min(20.0f, newSat));

                player.setFoodLevel(newFood);
                player.setSaturation(newSat);
            } catch (Exception ignored) {
            }
        });

        return ActionResult.ALLOW;
    }
}
