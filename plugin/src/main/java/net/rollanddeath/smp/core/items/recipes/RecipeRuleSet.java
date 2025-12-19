package net.rollanddeath.smp.core.items.recipes;

import net.rollanddeath.smp.core.items.CustomItemType;

public record RecipeRuleSet(
    CustomItemType resultCustomType,
    RecipeRulePhase prepare,
    RecipeRulePhase craft
) {
}
