package net.rollanddeath.smp.core.items.recipes;

public record RecipeRuleSet(
    String resultCustomId,
    RecipeRulePhase prepare,
    RecipeRulePhase craft
) {
}
