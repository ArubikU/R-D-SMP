package net.rollanddeath.smp.core.rules;

public class DayRule {
    private final int day;
    private final String name;
    private final String description;
    private final RuleType type;
    private final double value;

    public DayRule(int day, String name, String description, RuleType type, double value) {
        this.day = day;
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
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
}
