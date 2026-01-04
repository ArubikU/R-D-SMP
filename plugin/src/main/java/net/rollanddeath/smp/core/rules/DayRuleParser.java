package net.rollanddeath.smp.core.rules;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.scripted.ModifierRule;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.builtin.actions.ActionRegistrar;
import net.rollanddeath.smp.core.scripting.builtin.conditions.ConditionRegistrar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class DayRuleParser {

    private DayRuleParser() {
    }

    public record Result(List<DayRule> rules, int baseSleepPercentage) {
    }

    public static Result parse(RollAndDeathSMP plugin, ConfigurationSection root) {
        int baseSleep = 30;
        if (root != null) {
            ConfigurationSection settings = root.getConfigurationSection("settings");
            baseSleep = settings != null ? settings.getInt("base_sleep_percentage", 30) : 30;
        }

        if (root == null) {
            return new Result(Collections.emptyList(), baseSleep);
        }

        ConfigurationSection rulesSec = root.getConfigurationSection("day_rules");
        if (rulesSec == null) {
            return new Result(Collections.emptyList(), baseSleep);
        }

        List<DayRule> rules = new ArrayList<>();

        for (String key : rulesSec.getKeys(false)) {
            ConfigurationSection sec = rulesSec.getConfigurationSection(key);
            if (sec == null) continue;

            int day = sec.getInt("day", parseDay(key));
            String name = sec.getString("name");
            String description = sec.getString("description", "");
            if (day <= 0 || name == null || name.isBlank()) {
                warn(plugin, "Regla inválida en day_rules." + key + " (day/name)");
                continue;
            }

            String typeRaw = sec.getString("type", "NONE");
            RuleType type = RuleType.NONE;
            try {
                type = RuleType.valueOf(typeRaw.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                warn(plugin, "Tipo de regla desconocido '" + typeRaw + "' para day=" + day + ". Usando NONE.");
            }

            double value = sec.getDouble("value", 0.0);

            ModifierRule onActivate = parseRule(sec.getConfigurationSection("scripts.on_activate"));
            Map<String, ModifierRule> events = parseEvents(sec.getConfigurationSection("scripts.events"));
            Set<EntityType> restricted = parseEntityTypes(sec.getStringList("restricted_loot"));

            if (onActivate == null) {
                onActivate = buildDefaultBroadcastRule(day, name, description);
            }

            rules.add(new DayRule(day, name, description, type, value, onActivate, events, restricted));
        }

        rules.sort(Comparator.comparingInt(DayRule::getDay));
        return new Result(rules, baseSleep);
    }

    private static int parseDay(String key) {
        try {
            return Integer.parseInt(key);
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private static ModifierRule parseRule(ConfigurationSection sec) {
        if (sec == null) return null;

        boolean denyOnFail = sec.getBoolean("deny_on_fail", false);

        List<Condition> requireAll = new ArrayList<>();
        for (Map<?, ?> raw : sec.getMapList("require_all")) {
            Condition c = ConditionRegistrar.parse(raw);
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

    private static Map<String, ModifierRule> parseEvents(ConfigurationSection sec) {
        if (sec == null) return Collections.emptyMap();

        Map<String, ModifierRule> events = new HashMap<>();
        for (String eventName : sec.getKeys(false)) {
            ConfigurationSection eventSec = sec.getConfigurationSection(eventName);
            if (eventSec == null) continue;
            ModifierRule rule = parseRule(eventSec);
            if (rule != null) {
                events.put(eventName.trim().toLowerCase(Locale.ROOT), rule);
            }
        }
        return events;
    }

    private static Set<EntityType> parseEntityTypes(List<String> raw) {
        if (raw == null || raw.isEmpty()) return Collections.emptySet();
        Set<EntityType> out = new HashSet<>();
        for (String id : raw) {
            try {
                EntityType type = EntityType.valueOf(id.trim().toUpperCase(Locale.ROOT));
                out.add(type);
            } catch (IllegalArgumentException ignored) {
                // Skip unknown entity types
            }
        }
        return out;
    }

    private static ModifierRule buildDefaultBroadcastRule(int day, String name, String description) {
        List<Action> onPass = new ArrayList<>();
        Action title = ActionRegistrar.parse(Map.of(
            "type", "broadcast",
            "message", "Regla del Día " + day + ": " + name,
            "color", "gold"
        ));
        Action desc = ActionRegistrar.parse(Map.of(
            "type", "broadcast",
            "message", description,
            "color", "yellow"
        ));
        if (title != null) onPass.add(title);
        if (desc != null && description != null && !description.isBlank()) onPass.add(desc);

        return new ModifierRule(false, Collections.emptyList(), Collections.emptyList(), onPass);
    }

    private static void warn(RollAndDeathSMP plugin, String message) {
        if (plugin != null) {
            plugin.getLogger().warning(message);
        }
    }
}
