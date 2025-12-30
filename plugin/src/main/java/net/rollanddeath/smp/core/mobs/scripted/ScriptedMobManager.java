package net.rollanddeath.smp.core.mobs.scripted;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.MobManager;
import net.rollanddeath.smp.core.scripting.lint.ScriptLinter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;

public final class ScriptedMobManager {

    private final RollAndDeathSMP plugin;
    private final ScriptedMobRuntime runtime;
    private final Map<Integer, String> dailyThemes = new HashMap<>();
    private String defaultDailyTheme;

    public ScriptedMobManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.runtime = new ScriptedMobRuntime(plugin);
    }

    public ScriptedMobRuntime runtime() {
        return runtime;
    }

    public String getDailyThemeForDay(int day) {
        if (day <= 0) day = 1;
        String v = dailyThemes.get(day);
        if (v != null && !v.isBlank()) return v;
        return (defaultDailyTheme != null && !defaultDailyTheme.isBlank()) ? defaultDailyTheme : null;
    }

    public Map<Integer, String> getDailyThemesSnapshot() {
        return Collections.unmodifiableMap(new HashMap<>(dailyThemes));
    }

    public void loadAndRegister(MobManager mobManager) {
        try {
            if (!plugin.getDataFolder().exists()) {
                //noinspection ResultOfMethodCallIgnored
                plugin.getDataFolder().mkdirs();
            }

            File file = new File(plugin.getDataFolder(), "mobs.yml");
            if (!file.exists()) {
                plugin.saveResource("mobs.yml", false);
            }

            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

            // Lint temprano para detectar referencias inválidas en scripts
            ScriptLinter.logIssues(plugin.getLogger(), ScriptLinter.lintConfiguration("mobs.yml", cfg));

            // Daily theme metadata (tema del día)
            dailyThemes.clear();
            defaultDailyTheme = null;
            try {
                Object defObj = cfg.get("daily_theme_default");
                if (defObj instanceof String s && !s.isBlank()) {
                    defaultDailyTheme = s;
                }

                var section = cfg.getConfigurationSection("daily_themes");
                if (section == null) {
                    // compat: alias
                    section = cfg.getConfigurationSection("daily_theme");
                }
                if (section != null) {
                    for (String key : section.getKeys(false)) {
                        if (key == null || key.isBlank()) continue;
                        int day;
                        try {
                            day = Integer.parseInt(key.trim());
                        } catch (Exception ignored) {
                            continue;
                        }
                        if (day <= 0) continue;
                        String theme = section.getString(key);
                        if (theme == null || theme.isBlank()) continue;
                        dailyThemes.put(day, theme);
                    }
                }
            } catch (Exception ignored) {
            }

            Map<String, ScriptedMobDefinition> defsById = ScriptedMobParser.parseAll(cfg);

            for (ScriptedMobDefinition def : defsById.values()) {
                ScriptedMob mob = new ScriptedMob(plugin, def);
                mobManager.registerMob(mob);
            }

            runtime.stop();
            runtime.setDefinitions(defsById);
            if (!defsById.isEmpty()) {
                runtime.start();
                plugin.getLogger().info("Cargados ScriptedMobs desde mobs.yml: " + defsById.size());
            }

            // Re-evaluar la rotación diaria para respetar spawn_day/spawn_rate de mobs.yml
            try {
                if (plugin.getDailyMobRotationManager() != null && plugin.getGameManager() != null) {
                    plugin.getDailyMobRotationManager().refreshForDay(plugin.getGameManager().getCurrentDay());
                }
            } catch (Exception ignored) {
            }
        } catch (Exception e) {
            plugin.getLogger().warning("No se pudo cargar mobs.yml: " + e.getMessage());
        }
    }
}
