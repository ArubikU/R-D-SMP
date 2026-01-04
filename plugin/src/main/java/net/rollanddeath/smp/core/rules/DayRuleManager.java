package net.rollanddeath.smp.core.rules;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.scripted.ModifierRule;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import net.rollanddeath.smp.core.scripting.ScriptPhase;
import net.rollanddeath.smp.core.scripting.ScriptVars;
import org.bukkit.GameRule;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DayRuleManager {

    private final RollAndDeathSMP plugin;
    private final List<DayRule> rules;
    private int baseSleepPercentage = 30;

    public DayRuleManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.rules = new ArrayList<>();
        loadRules();
    }

    private void loadRules() {
        rules.clear();

        ConfigurationSection cfg = loadMergedConfiguration();
        DayRuleParser.Result parsed = DayRuleParser.parse(plugin, cfg);

        if (parsed.rules().isEmpty()) {
            plugin.getLogger().warning("No se encontraron reglas en rules.yml; usando configuración vacía.");
        } else {
            rules.addAll(parsed.rules());
        }

        this.baseSleepPercentage = parsed.baseSleepPercentage();
    }

    private ConfigurationSection loadMergedConfiguration() {
        File file = new File(plugin.getDataFolder(), "rules.yml");
        if (!file.exists()) {
            plugin.saveResource("rules.yml", false);
        }

        YamlConfiguration merged = new YamlConfiguration();

        try (InputStream in = plugin.getResource("rules.yml")) {
            if (in != null) {
                YamlConfiguration defaults = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
                merged.addDefaults(defaults);
                merged.options().copyDefaults(true);
            }
        } catch (Exception ignored) {
        }

        YamlConfiguration userCfg = YamlConfiguration.loadConfiguration(file);
        for (String key : userCfg.getKeys(true)) {
            if (userCfg.isConfigurationSection(key)) continue;
            merged.set(key, userCfg.get(key));
        }

        return merged;
    }

    public List<DayRule> getActiveRules(int currentDay) {
        return rules.stream()
                .filter(rule -> rule.getDay() == currentDay)
                .collect(Collectors.toList());
    }

    public List<DayRule> getRulesUpTo(int currentDay) {
        return rules.stream()
                .filter(rule -> rule.getDay() <= currentDay)
                .sorted((a, b) -> Integer.compare(a.getDay(), b.getDay()))
                .collect(Collectors.toList());
    }

    public boolean isRuleActive(int currentDay, RuleType type) {
        return rules.stream()
                .anyMatch(rule -> rule.getDay() == currentDay && rule.getType() == type);
    }

    public double getCumulativeValue(int currentDay, RuleType type) {
        return rules.stream()
                .filter(rule -> rule.getDay() <= currentDay && rule.getType() == type)
                .mapToDouble(DayRule::getValue)
                .sum();
    }

    public double getLatestValue(int currentDay, RuleType type) {
        return rules.stream()
                .filter(rule -> rule.getDay() <= currentDay && rule.getType() == type)
                .max((r1, r2) -> Integer.compare(r1.getDay(), r2.getDay()))
                .map(DayRule::getValue)
                .orElse(0.0);
    }

    public void addRule(DayRule rule) {
        rules.add(rule);
    }

    public void refreshForDay(int day) {
        List<DayRule> active = getActiveRules(day);

        plugin.getServer().getWorlds().forEach(world -> world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, baseSleepPercentage));

        if (!active.isEmpty()) {
            for (DayRule rule : active) {
                if (!runActivation(rule, day)) {
                    continue;
                }

                if (rule.getType() == RuleType.SLEEP_PERCENTAGE) {
                    int percentage = (int) rule.getValue();
                    plugin.getServer().getWorlds().forEach(world -> world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, percentage));
                }
            }
        }
    }

    public boolean runScripts(String eventName, Map<String, Object> vars) {
        if (eventName == null || eventName.isBlank()) {
            return false;
        }

        boolean deny = false;
        int currentDay = plugin.getGameManager().getCurrentDay();
        Player player = resolvePlayer(vars);

        for (DayRule rule : getRulesUpTo(currentDay)) {
            Map<String, ModifierRule> scripts = rule.getEvents();
            if (scripts == null) continue;
            ModifierRule script = scripts.get(eventName.toLowerCase(Locale.ROOT));
            if (script == null) continue;
            if (applyRule(rule, script, eventName, player, vars)) {
                deny = true;
            }
        }

        return deny;
    }

    public Set<EntityType> getRestrictedMobs(int day) {
        Set<EntityType> restricted = new HashSet<>();
        for (DayRule rule : getRulesUpTo(day)) {
            if (rule.getType() != RuleType.LOOT_RESTRICTION) continue;
            restricted.addAll(rule.getRestrictedMobs());
        }
        return restricted;
    }

    private boolean runActivation(DayRule rule, int day) {
        ModifierRule activation = rule.getOnActivate();
        if (activation == null) {
            return true;
        }

        Map<String, Object> vars = ScriptVars.create()
                .putInternal("EVENT.args.day", day)
                .putInternal("EVENT.args.rule_name", rule.getName())
                .putInternal("EVENT.args.rule_description", rule.getDescription())
                .build();

        return !applyRule(rule, activation, "day_start", null, vars);
    }

    private boolean applyRule(DayRule dayRule, ModifierRule script, String subject, Player player, Map<String, Object> vars) {
        Map<String, Object> scopedVars = new HashMap<>();
        if (vars != null) scopedVars.putAll(vars);
        scopedVars.put("EVENT.args.rule_day", dayRule.getDay());
        scopedVars.put("EVENT.args.rule_name", dayRule.getName());
        scopedVars.put("EVENT.args.rule_description", dayRule.getDescription());

        String subjectId = "day_rule:" + dayRule.getDay() + ":" + subject;
        ScriptContext ctx = new ScriptContext(plugin, player, subjectId, ScriptPhase.RULE, scopedVars);

        boolean pass = ScriptEngine.allConditionsPass(ctx, script.requireAll());
        if (pass) {
            ActionResult r = ScriptEngine.runAllWithResult(ctx, script.onPass());
            return r != null && r.deny();
        }

        ActionResult r = ScriptEngine.runAllWithResult(ctx, script.onFail());
        return script.denyOnFail() || (r != null && r.deny());
    }

    private Player resolvePlayer(Map<String, Object> vars) {
        if (vars == null) return null;
        Object subject = vars.get("__subject");
        if (subject instanceof Player p) return p;
        Object target = vars.get("__target");
        if (target instanceof Player p) return p;
        return null;
    }
}
