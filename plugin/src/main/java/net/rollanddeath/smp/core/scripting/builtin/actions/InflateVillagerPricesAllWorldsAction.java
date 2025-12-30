package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.MerchantRecipe;

final class InflateVillagerPricesAllWorldsAction {
    private InflateVillagerPricesAllWorldsAction() {}

    static void register() {
        ActionRegistrar.register("inflate_villager_prices_all_worlds", InflateVillagerPricesAllWorldsAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Integer mult = Resolvers.integer(null, raw, "multiplier");
        if (mult == null || mult < 2) return null;
        String key = Resolvers.string(null, raw, "key");
        if (key == null) key = "inflated_prices";
        
        final String finalKey = key;
        final int m = mult;

        return ctx -> {
            ActionUtils.runSync(ctx.plugin(), () -> {
                for (World w : Bukkit.getWorlds()) {
                    for (Villager v : w.getEntitiesByClass(Villager.class)) {
                        if (v.getScoreboardTags().contains(finalKey)) continue;
                        
                        boolean modified = false;
                        java.util.List<MerchantRecipe> recipes = new java.util.ArrayList<>(v.getRecipes());
                        for (MerchantRecipe r : recipes) {
                            var ingredients = r.getIngredients();
                            if (ingredients.isEmpty()) continue;
                            
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
                            r.setIngredients(ingredients);
                        }
                        
                        if (modified) {
                            v.setRecipes(recipes);
                            v.addScoreboardTag(finalKey);
                        }
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
