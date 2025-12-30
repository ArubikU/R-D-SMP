package net.rollanddeath.smp.core.scripting.scope.modifiers;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.scope.ScopeId;

import java.util.List;

public record ScopedModifier(
    ScopeId scope,
    String id,
    int priority,
    Condition when,
    List<Action> effects
) {
}
