package net.rollanddeath.smp.core.modifiers.scripted;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.builtin.actions.ActionRegistrar;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInConditions;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ScriptedModifierParser {

    private ScriptedModifierParser() {
    }

    public static Map<String, ScriptedModifierDefinition> parseAll(ConfigurationSection root) {
        Map<String, ScriptedModifierDefinition> out = new HashMap<>();
        if (root == null) return out;

        ConfigurationSection modifiers = root.getConfigurationSection("modifiers");
        if (modifiers == null) return out;

        for (String id : modifiers.getKeys(false)) {
            ConfigurationSection sec = modifiers.getConfigurationSection(id);
            if (sec == null) continue;

            String name = sec.getString("name");
            String typeRaw = sec.getString("type");
            String description = sec.getString("description", "");
            if (name == null || name.isBlank() || typeRaw == null || typeRaw.isBlank()) continue;

            String type = typeRaw.trim().toUpperCase(Locale.ROOT);

            Map<String, ModifierRule> events = new HashMap<>();
            ConfigurationSection eventsSec = sec.getConfigurationSection("events");
            if (eventsSec != null) {
                for (String eventName : eventsSec.getKeys(false)) {
                    ConfigurationSection eventSec = eventsSec.getConfigurationSection(eventName);
                    if (eventSec == null) continue;
                    ModifierRule rule = parseRule(eventSec);
                    if (rule != null) {
                        events.put(eventName.trim().toLowerCase(Locale.ROOT), rule);
                    }
                }
            }

            out.put(id, new ScriptedModifierDefinition(id, name, type, description, events));
        }

        return out;
    }

    private static ModifierRule parseRule(ConfigurationSection sec) {
        boolean denyOnFail = sec.getBoolean("deny_on_fail", false);

        List<Condition> requireAll = new ArrayList<>();
        for (Map<?, ?> raw : sec.getMapList("require_all")) {
            Condition c = BuiltInConditions.parse(raw);
            if (c != null) requireAll.add(c);
        }

        List<Action> onFail = new ArrayList<>();
        for (Map<?, ?> raw : sec.getMapList("on_fail")) {
            Action a = ActionRegistrar.parse(raw);
            if (a != null) onFail.add(a);
        }

        List<Action> onPass = new ArrayList<>();
        for (Map<?, ?> raw : sec.getMapList("on_pass")) {
            Action a = ActionRegistrar.parse(raw);
            if (a != null) onPass.add(a);
        }

        return new ModifierRule(denyOnFail, requireAll, onFail, onPass);
    }
}
