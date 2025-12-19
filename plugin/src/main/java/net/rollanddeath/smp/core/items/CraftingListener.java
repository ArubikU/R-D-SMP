package net.rollanddeath.smp.core.items;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.recipes.RecipeRulePhase;
import net.rollanddeath.smp.core.items.recipes.RecipeRuleSet;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import net.rollanddeath.smp.core.scripting.ScriptPhase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.NamespacedKey;
import org.bukkit.Keyed;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CraftingListener implements Listener {

    private final RollAndDeathSMP plugin;

    public CraftingListener(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;
        Recipe recipe = event.getRecipe();
        ItemStack result = recipe.getResult();

        CustomItem item = getCustomItemFromStack(result);
        if (item == null) return;

        if (!(event.getView().getPlayer() instanceof Player player)) return;

        // Restricciones de crafting se manejan desde recipes.yml (rules).

        NamespacedKey key = (recipe instanceof Keyed keyed) ? keyed.getKey() : null;
        if (key == null) return;

        RecipeRuleSet rules = plugin.getRecipeManager().getRuleSet(key);
        if (rules == null || rules.prepare() == null) return;

        boolean deny = applyRulesPhase(rules.prepare(), player, key, ScriptPhase.PREPARE);
        if (deny) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (event.getRecipe() == null) return;
        Recipe recipe = event.getRecipe();
        ItemStack result = recipe.getResult();

        CustomItem item = getCustomItemFromStack(result);
        if (item == null) return;

        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Restricciones de crafting se manejan desde recipes.yml (rules).

        NamespacedKey key = (recipe instanceof Keyed keyed) ? keyed.getKey() : null;
        if (key == null) return;

        RecipeRuleSet rules = plugin.getRecipeManager().getRuleSet(key);
        if (rules == null || rules.craft() == null) return;

        boolean deny = applyRulesPhase(rules.craft(), player, key, ScriptPhase.CRAFT);
        if (deny) {
            event.setCancelled(true);
        }
    }

    private boolean applyRulesPhase(RecipeRulePhase phase, Player player, NamespacedKey recipeKey, ScriptPhase scriptPhase) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("recipe_key", recipeKey.getKey());
        vars.put("recipe_namespace", recipeKey.getNamespace());

        ScriptContext ctx = new ScriptContext(plugin, player, recipeKey.toString(), scriptPhase, vars);

        boolean pass = ScriptEngine.allConditionsPass(ctx, phase.requireAll());
        if (pass) {
            ActionResult r = ScriptEngine.runAllWithResult(ctx, phase.onPass());
            return r != null && r.deny();
        }

        ActionResult r = ScriptEngine.runAllWithResult(ctx, phase.onFail());
        return phase.denyOnFail() || (r != null && r.deny());
    }

    private CustomItem getCustomItemFromStack(ItemStack stack) {
        for (CustomItemType type : CustomItemType.values()) {
            CustomItem item = plugin.getItemManager().getItem(type);
            if (item != null && item.getItemStack().isSimilar(stack)) {
                return item;
            }
        }
        return null;
    }

}
