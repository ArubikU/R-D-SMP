package net.rollanddeath.smp.core.rules;

import org.bukkit.entity.EntityType;

import java.util.EnumSet;
import java.util.Set;

public class DayRuleState {

    private int sleepPercent = 30;
    private boolean sleepBlocked = false;
    private int extraNaturalSpawns = 0;
    private int spiderEffectsMin = 0;
    private int spiderEffectsMax = 0;
    private final Set<EntityType> dropBlacklist = EnumSet.noneOf(EntityType.class);
    private double ravagerTotemChance = 0.0;
    private double damageMultiplier = 1.0;

    public int getSleepPercent() {
        return sleepPercent;
    }

    public void setSleepPercent(int sleepPercent) {
        this.sleepPercent = Math.max(1, Math.min(100, sleepPercent));
    }

    public boolean isSleepBlocked() {
        return sleepBlocked;
    }

    public void setSleepBlocked(boolean sleepBlocked) {
        this.sleepBlocked = sleepBlocked;
    }

    public int getExtraNaturalSpawns() {
        return extraNaturalSpawns;
    }

    public void addExtraNaturalSpawns(int extra) {
        this.extraNaturalSpawns = Math.max(0, this.extraNaturalSpawns + extra);
    }

    public int getSpiderEffectsMin() {
        return spiderEffectsMin;
    }

    public int getSpiderEffectsMax() {
        return spiderEffectsMax;
    }

    public void setSpiderEffectRange(int min, int max) {
        this.spiderEffectsMin = Math.max(0, min);
        this.spiderEffectsMax = Math.max(this.spiderEffectsMin, max);
    }

    public Set<EntityType> getDropBlacklist() {
        return dropBlacklist;
    }

    public void addDropBlacklist(Set<EntityType> entities) {
        this.dropBlacklist.addAll(entities);
    }

    public double getRavagerTotemChance() {
        return ravagerTotemChance;
    }

    public void setRavagerTotemChance(double ravagerTotemChance) {
        this.ravagerTotemChance = Math.max(0.0, Math.min(1.0, ravagerTotemChance));
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public void addDamageScale(double extraScale) {
        this.damageMultiplier *= (1.0 + Math.max(-0.99, extraScale));
    }
}
