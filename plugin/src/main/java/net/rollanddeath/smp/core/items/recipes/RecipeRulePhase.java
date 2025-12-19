package net.rollanddeath.smp.core.items.recipes;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Condition;

import java.util.List;

public record RecipeRulePhase(
    boolean denyOnFail,
    List<Condition> requireAll,
    List<Action> onFail,
    List<Action> onPass
) {
}
