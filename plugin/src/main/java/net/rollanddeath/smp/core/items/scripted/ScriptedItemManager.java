package net.rollanddeath.smp.core.items.scripted;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.ItemManager;
import net.rollanddeath.smp.core.scripting.lint.ScriptLinter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class ScriptedItemManager {

    private final RollAndDeathSMP plugin;

    public ScriptedItemManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    public void loadAndRegister(ItemManager itemManager) {
        try {
            if (!plugin.getDataFolder().exists()) {
                //noinspection ResultOfMethodCallIgnored
                plugin.getDataFolder().mkdirs();
            }

            File file = new File(plugin.getDataFolder(), "items.yml");
            if (!file.exists()) {
                plugin.saveResource("items.yml", false);
            }

            // Merge: defaults del JAR + overrides del data-folder.
            // Esto permite que, al actualizar el plugin, los ítems nuevos aparezcan aunque el servidor ya tenga items.yml.
            YamlConfiguration merged = new YamlConfiguration();

            try (InputStream in = plugin.getResource("items.yml")) {
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

            // Lint temprano para detectar referencias inválidas en scripts
            ScriptLinter.logIssues(plugin.getLogger(), ScriptLinter.lintConfiguration("items.yml (merged)", merged));

            Map<String, ScriptedItemDefinition> defs = ScriptedItemParser.parseAll(merged);

            int registered = 0;
            for (ScriptedItemDefinition def : defs.values()) {
                if (def == null || def.id() == null) continue;

                // Si ya existe, ItemManager se encarga de desregistrar el anterior y registrar el nuevo.
                itemManager.registerItem(new ScriptedItem(plugin, def));
                registered++;
            }

            if (registered > 0) {
                plugin.getLogger().info("Cargados items scripted desde items.yml: " + registered);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("No se pudo cargar items.yml: " + e.getMessage());
        }
    }
}
