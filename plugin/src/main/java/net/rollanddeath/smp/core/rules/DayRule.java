package net.rollanddeath.smp.core.rules;

import java.util.function.Consumer;

public class DayRule {

    private final int day;
    private final String name;
    private final String description;
    private final Consumer<DayRuleState> applier;

    public DayRule(int day, String name, String description, Consumer<DayRuleState> applier) {
        this.day = day;
        this.name = name;
        this.description = description;
        this.applier = applier;
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

    public void apply(DayRuleState state) {
        applier.accept(state);
    }
}
