package net.rollanddeath.smp.core.items.recipes;

import net.rollanddeath.smp.core.items.CustomItemType;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInConditions;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class RecipeRuleParser {

    private RecipeRuleParser() {
    }

    public static RecipeRuleSet parseRuleSet(ConfigurationSection recipeSection) {
        if (recipeSection == null) return null;

        ConfigurationSection rules = recipeSection.getConfigurationSection("rules");
        if (rules == null) return null;

        CustomItemType resultCustom = null;
        ConfigurationSection resultSection = recipeSection.getConfigurationSection("result");
        if (resultSection != null) {
            String custom = resultSection.getString("custom");
            if (custom != null && !custom.isBlank()) {
                try {
                    resultCustom = CustomItemType.valueOf(custom.trim().toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException ignored) {
                    // ignore unknown
                }
            }
        }

        RecipeRulePhase prepare = parsePhase(rules.getConfigurationSection("prepare"));
        RecipeRulePhase craft = parsePhase(rules.getConfigurationSection("craft"));
        if (prepare == null && craft == null) return null;

        return new RecipeRuleSet(resultCustom, prepare, craft);
    }

    private static RecipeRulePhase parsePhase(ConfigurationSection phaseSection) {
        if (phaseSection == null) return null;

        boolean denyOnFail = phaseSection.getBoolean("deny_on_fail", true);

        List<Condition> requireAll = new ArrayList<>();
        for (Map<?, ?> raw : phaseSection.getMapList("require_all")) {
            Condition c = BuiltInConditions.parse(raw);
            if (c != null) requireAll.add(c);
        }

        List<Action> onFail = new ArrayList<>();
        for (Map<?, ?> raw : phaseSection.getMapList("on_fail")) {
            Action a = BuiltInActions.parse(raw);
            if (a != null) onFail.add(a);
        }

        List<Action> onPass = new ArrayList<>();
        for (Map<?, ?> raw : phaseSection.getMapList("on_pass")) {
            Action a = BuiltInActions.parse(raw);
            if (a != null) onPass.add(a);
        }

        return new RecipeRulePhase(denyOnFail, requireAll, onFail, onPass);
    }
}
