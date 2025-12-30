package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MerchantRecipe;

final class DeflateVillagerPricesAllWorldsAction {
    private DeflateVillagerPricesAllWorldsAction() {}

    static void register() {
        ActionRegistrar.register("deflate_villager_prices_all_worlds", DeflateVillagerPricesAllWorldsAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Integer div = Resolvers.integer(null, raw, "divisor");
        if (div == null || div < 2) return null;
        String key = Resolvers.string(null, raw, "key");
        if (key == null) key = "inflated_prices";

        final String finalKey = key;
        final int d = div;

        return ctx -> {
            ActionUtils.runSync(ctx.plugin(), () -> {
                for (World w : Bukkit.getWorlds()) {
                    for (Villager v : w.getEntitiesByClass(Villager.class)) {
                        if (!v.getScoreboardTags().contains(finalKey)) continue;
                        
                        boolean modified = false;
                        java.util.List<MerchantRecipe> recipes = new java.util.ArrayList<>(v.getRecipes());
                        for (MerchantRecipe r : recipes) {
                            var ingredients = r.getIngredients();
                            if (ingredients.isEmpty()) continue;
                            
                            var first = ingredients.get(0);
                            if (first.getType() == org.bukkit.Material.EMERALD) {
                                first.setAmount(Math.max(1, first.getAmount() / d));
                                modified = true;
                            }
                            if (ingredients.size() > 1) {
                                var second = ingredients.get(1);
                                if (second.getType() == org.bukkit.Material.EMERALD) {
                                    second.setAmount(Math.max(1, second.getAmount() / d));
                                    modified = true;
                                }
                            }
                            r.setIngredients(ingredients);
                        }
                        
                        if (modified) {
                            v.setRecipes(recipes);
                            v.removeScoreboardTag(finalKey);
                        }
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
