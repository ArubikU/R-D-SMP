package net.rollanddeath.smp.core.modifiers.scripted;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Condition;

import java.util.List;

public record ModifierRule(
    boolean denyOnFail,
    List<Condition> requireAll,
    List<Action> onFail,
    List<Action> onPass
) {
}
