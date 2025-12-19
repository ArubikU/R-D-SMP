package net.rollanddeath.smp.core.modifiers.scripted;

import net.rollanddeath.smp.core.modifiers.ModifierType;

import java.util.Map;

public record ScriptedModifierDefinition(
    String id,
    String name,
    ModifierType type,
    String description,
    Map<String, ModifierRule> events
) {
}
