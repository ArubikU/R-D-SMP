package net.rollanddeath.smp.core.scripting.library;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.builtin.actions.ActionRegistrar;
import net.rollanddeath.smp.core.scripting.builtin.conditions.ConditionRegistrar;
import net.rollanddeath.smp.core.scripting.lint.ScriptLintIssue;
import net.rollanddeath.smp.core.scripting.lint.ScriptLinter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ScriptLibraryLoader {

    private ScriptLibraryLoader() {
    }

    public static ScriptLibrary load(RollAndDeathSMP plugin) {
        if (plugin == null) return new ScriptLibrary(Map.of(), Map.of());

        try {
            if (!plugin.getDataFolder().exists()) {
                //noinspection ResultOfMethodCallIgnored
                plugin.getDataFolder().mkdirs();
            }

            File file = new File(plugin.getDataFolder(), "scripts.yml");
            if (!file.exists()) {
                plugin.saveResource("scripts.yml", false);
            }

            // Merge: defaults del JAR + overrides del data-folder
            YamlConfiguration merged = new YamlConfiguration();

            try (InputStream in = plugin.getResource("scripts.yml")) {
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

            try {
                List<ScriptLintIssue> issues = ScriptLinter.lintConfiguration("scripts.yml (merged)", merged);
                ScriptLinter.logIssues(plugin.getLogger(), issues);
            } catch (Exception e) {
                plugin.getLogger().warning("Script linter failed for scripts.yml: " + e.getMessage());
            }

            return parseMerged(plugin, merged);
        } catch (Exception e) {
            plugin.getLogger().warning("No se pudo cargar scripts.yml: " + e.getMessage());
            return new ScriptLibrary(Map.of(), Map.of());
        }
    }

    private static ScriptLibrary parseMerged(RollAndDeathSMP plugin, YamlConfiguration cfg) {
        ConfigurationSection root = cfg.getConfigurationSection("script_library");
        if (root == null) return new ScriptLibrary(Map.of(), Map.of());

        Map<String, List<Action>> actions = new HashMap<>();
        ConfigurationSection actionsSec = root.getConfigurationSection("actions");
        if (actionsSec != null) {
            for (String id : actionsSec.getKeys(false)) {
                Object raw = actionsSec.get(id);
                if (!(raw instanceof List<?> list) || list.isEmpty()) continue;
                List<Action> parsed = new ArrayList<>();
                for (Object o : list) {
                    if (!(o instanceof Map<?, ?> m)) continue;
                    Action a = ActionRegistrar.parse(m);
                    if (a != null) parsed.add(a);
                }
                if (!parsed.isEmpty()) actions.put(id, List.copyOf(parsed));
            }
        }

        Map<String, Condition> conditions = new HashMap<>();
        ConfigurationSection condSec = root.getConfigurationSection("conditions");
        if (condSec != null) {
            for (String id : condSec.getKeys(false)) {
                Object raw = condSec.get(id);
                if (!(raw instanceof Map<?, ?> m)) continue;
                Condition c = ConditionRegistrar.parse(m);
                if (c != null) conditions.put(id, c);
            }
        }

        int aCount = actions.size();
        int cCount = conditions.size();
        if (aCount > 0 || cCount > 0) {
            plugin.getLogger().info("Cargada ScriptLibrary desde scripts.yml: actions=" + aCount + ", conditions=" + cCount);
        }

        return new ScriptLibrary(actions, conditions);
    }
}
