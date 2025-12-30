package net.rollanddeath.smp.core.modifiers.scripted;

import java.util.Map;

public record ScriptedModifierDefinition(
    String id,
    String name,
    String type,
    String description,
    Map<String, ModifierRule> events
) {
}
