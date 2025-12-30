package net.rollanddeath.smp.core.scripting.library;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Condition;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Librería global de bloques reutilizables para scripting.
 *
 * - actions: id -> lista de acciones
 * - conditions: id -> condición (árbol)
 */
public final class ScriptLibrary {

    private final Map<String, List<Action>> actions;
    private final Map<String, Condition> conditions;

    public ScriptLibrary(Map<String, List<Action>> actions, Map<String, Condition> conditions) {
        this.actions = actions != null ? actions : Collections.emptyMap();
        this.conditions = conditions != null ? conditions : Collections.emptyMap();
    }

    public List<Action> getActions(String id) {
        if (id == null) return null;
        return actions.get(id);
    }

    public Condition getCondition(String id) {
        if (id == null) return null;
        return conditions.get(id);
    }

    public Map<String, List<Action>> actions() {
        return actions;
    }

    public Map<String, Condition> conditions() {
        return conditions;
    }
}
