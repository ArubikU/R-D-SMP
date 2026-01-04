package net.rollanddeath.smp.core.rules;

import net.rollanddeath.smp.core.modifiers.scripted.ModifierRule;
import org.bukkit.entity.EntityType;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class DayRule {
    private final int day;
    private final String name;
    private final String description;
    private final RuleType type;
    private final double value;
    private final ModifierRule onActivate;
    private final Map<String, ModifierRule> events;
    private final Set<EntityType> restrictedMobs;

    public DayRule(int day, String name, String description, RuleType type, double value) {
        this(day, name, description, type, value, null, Collections.emptyMap(), Collections.emptySet());
    }

    public DayRule(int day, String name, String description, RuleType type, double value, ModifierRule onActivate, Map<String, ModifierRule> events, Set<EntityType> restrictedMobs) {
        this.day = day;
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
        this.onActivate = onActivate;
        this.events = events != null ? events : Collections.emptyMap();
        this.restrictedMobs = restrictedMobs != null ? restrictedMobs : Collections.emptySet();
    }

    public DayRule(int day, String name, String description, RuleType type) {
        this(day, name, description, type, 0.0);
    }

    public int getDay() {
        return day;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public RuleType getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public ModifierRule getOnActivate() {
        return onActivate;
    }

    public Map<String, ModifierRule> getEvents() {
        return events;
    }

    public Set<EntityType> getRestrictedMobs() {
        return restrictedMobs;
    }
}
