package net.rollanddeath.smp.core.mobs.scripted;

import net.rollanddeath.smp.core.modifiers.scripted.ModifierRule;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.builtin.actions.ActionRegistrar;
import net.rollanddeath.smp.core.scripting.builtin.conditions.ConditionRegistrar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ScriptedMobParser {

    private ScriptedMobParser() {
    }

    public static Map<String, ScriptedMobDefinition> parseAll(ConfigurationSection root) {
        Map<String, ScriptedMobDefinition> out = new HashMap<>();
        if (root == null) return out;

        ConfigurationSection mobs = root.getConfigurationSection("mobs");
        if (mobs == null) return out;

        for (String id : mobs.getKeys(false)) {
            ConfigurationSection sec = mobs.getConfigurationSection(id);
            if (sec == null) continue;

            String entityTypeRaw = firstNonBlank(sec.getString("entity_type"), sec.getString("entity"));
            if (entityTypeRaw == null) continue;

            EntityType entityType;
            try {
                entityType = EntityType.valueOf(entityTypeRaw.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                continue;
            }

            String displayName = sec.getString("name");
            boolean isBoss = sec.getBoolean("is_boss", false);

            Integer spawnDay = null;
            if (sec.isInt("spawn_day")) {
                spawnDay = Math.max(1, sec.getInt("spawn_day"));
            }

            Double spawnRate = null;
            Object spawnRateObj = sec.get("spawn_rate");
            if (spawnRateObj instanceof Number n) {
                spawnRate = n.doubleValue();
            } else if (spawnRateObj instanceof String s) {
                try {
                    spawnRate = Double.parseDouble(s.trim());
                } catch (Exception ignored) {
                    spawnRate = null;
                }
            }
            if (spawnRate != null) {
                spawnRate = Math.max(0.0, Math.min(1.0, spawnRate));
            }

            Map<String, Double> attributes = new HashMap<>();
            ConfigurationSection attrSec = sec.getConfigurationSection("attributes");
            if (attrSec != null) {
                for (String k : attrSec.getKeys(false)) {
                    Object rawVal = attrSec.get(k);
                    if (!(rawVal instanceof Number n)) continue;
                    double v = n.doubleValue();
                    String key = k != null ? k.trim() : null;
                    if (key == null || key.isBlank()) continue;
                    attributes.put(key.toUpperCase(Locale.ROOT), v);
                }
            }

            ScriptedMobDefinition.Equipment equipment = null;
            ConfigurationSection eqSec = sec.getConfigurationSection("equipment");
            if (eqSec != null) {
                equipment = new ScriptedMobDefinition.Equipment(
                    eqSec.getString("helmet"),
                    eqSec.getString("chestplate"),
                    eqSec.getString("leggings"),
                    eqSec.getString("boots"),
                    firstNonBlank(eqSec.getString("main_hand"), eqSec.getString("mainHand")),
                    firstNonBlank(eqSec.getString("off_hand"), eqSec.getString("offHand"))
                );
            }

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

            List<ScriptedMobDefinition.LootEntry> loot = new ArrayList<>();
            for (Map<?, ?> raw : sec.getMapList("loot")) {
                Object matObj = raw.get("material");
                Object customObj = raw.get("custom_item");

                String material = (matObj instanceof String s) ? s : null;
                String customItem = (customObj instanceof String s) ? s : null;

                if (material != null && material.isBlank()) material = null;
                if (customItem != null && customItem.isBlank()) customItem = null;

                // Requiere al menos material o custom_item
                if (material == null && customItem == null) continue;

                int amount = 1;
                Object amountObj = raw.get("amount");
                if (amountObj instanceof Number n) amount = Math.max(1, n.intValue());

                double chance = 1.0;
                Object chanceObj = raw.get("chance");
                if (chanceObj instanceof Number n) chance = Math.max(0.0, Math.min(1.0, n.doubleValue()));

                loot.add(new ScriptedMobDefinition.LootEntry(material, customItem, amount, chance));
            }

            out.put(id, new ScriptedMobDefinition(id, entityType, displayName, spawnDay, spawnRate, isBoss, attributes, equipment, events, loot));
        }

        return out;
    }

    private static ModifierRule parseRule(ConfigurationSection sec) {
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

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) return a;
        if (b != null && !b.isBlank()) return b;
        return null;
    }
}
