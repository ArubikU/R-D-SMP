package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.MerchantRecipe;

final class MultiplyVillagerTradeCostAction {
    private MultiplyVillagerTradeCostAction() {}

    static void register() {
        ActionRegistrar.register("multiply_villager_trade_cost", MultiplyVillagerTradeCostAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Integer mult = Resolvers.integer(null, raw, "multiplier");
        if (mult == null || mult < 2) return null;
        final int m = mult;

        return ctx -> {
            VillagerAcquireTradeEvent vate = ctx.nativeEvent(VillagerAcquireTradeEvent.class);
            if (vate == null) return ActionResult.ALLOW;
            
            MerchantRecipe r = vate.getRecipe();
            var ingredients = r.getIngredients();
            if (ingredients.isEmpty()) return ActionResult.ALLOW;
            
            boolean modified = false;
            var first = ingredients.get(0);
            if (first.getType() == org.bukkit.Material.EMERALD) {
                first.setAmount(Math.min(64, first.getAmount() * m));
                modified = true;
            }
            if (ingredients.size() > 1) {
                var second = ingredients.get(1);
                if (second.getType() == org.bukkit.Material.EMERALD) {
                    second.setAmount(Math.min(64, second.getAmount() * m));
                    modified = true;
                }
            }
            
            if (modified) {
                r.setIngredients(ingredients);
                vate.setRecipe(r);
            }
            return ActionResult.ALLOW;
        };
    }
}
