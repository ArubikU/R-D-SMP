package net.rollanddeath.smp.core.items;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.impl.LokiDice;
import net.rollanddeath.smp.core.items.recipes.RecipeRuleParser;
import net.rollanddeath.smp.core.items.recipes.RecipeRuleSet;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RecipeManager {

    private final RollAndDeathSMP plugin;

        private final Map<NamespacedKey, RecipeRuleSet> rulesByKey = new HashMap<>();

    public RecipeManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

        public RecipeRuleSet getRuleSet(NamespacedKey key) {
                return rulesByKey.get(key);
        }

    public void registerRecipes() {
                ensureRecipesFile();
                File file = new File(plugin.getDataFolder(), "recipes.yml");
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

                rulesByKey.clear();

                ConfigurationSection root = yaml.getConfigurationSection("recipes");
                if (root == null) {
                        plugin.getLogger().warning("recipes.yml no contiene la sección 'recipes'.");
                        return;
                }

                int ok = 0;
                int fail = 0;

                for (String id : root.getKeys(false)) {
                        ConfigurationSection section = root.getConfigurationSection(id);
                        if (section == null) continue;

                        try {
                                registerRecipeFromSection(id, section);
                                ok++;
                        } catch (Exception e) {
                                fail++;
                                plugin.getLogger().warning("No se pudo registrar la receta '" + id + "': " + e.getMessage());
                        }
                }

                plugin.getLogger().info("Recetas cargadas: " + ok + " OK, " + fail + " con error.");
    }

        private void ensureRecipesFile() {
                if (!plugin.getDataFolder().exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        plugin.getDataFolder().mkdirs();
                }
                File file = new File(plugin.getDataFolder(), "recipes.yml");
                if (!file.exists()) {
                        plugin.saveResource("recipes.yml", false);
                }
        }

        private void registerRecipeFromSection(String id, ConfigurationSection section) {
                String type = section.getString("type", "").trim().toLowerCase(Locale.ROOT);
                NamespacedKey key = new NamespacedKey(plugin, id.toLowerCase(Locale.ROOT));

                plugin.getServer().removeRecipe(key);

                ItemStack result = resolveResultItem(section.getConfigurationSection("result"));
                if (result == null) {
                        throw new IllegalArgumentException("result inválido o faltante");
                }

                Recipe recipe = switch (type) {
                        case "shaped" -> buildShapedRecipe(key, result, section);
                        case "shapeless" -> buildShapelessRecipe(key, result, section);
                        case "furnace" -> buildFurnaceRecipe(key, result, section);
                        case "blasting" -> buildBlastingRecipe(key, result, section);
                        case "smoking" -> buildSmokingRecipe(key, result, section);
                        case "campfire" -> buildCampfireRecipe(key, result, section);
                        case "stonecutting" -> buildStonecuttingRecipe(key, result, section);
                        case "smithing_transform" -> buildSmithingTransformRecipe(key, result, section);
                        default -> throw new IllegalArgumentException("type no soportado: " + type);
                };

                RecipeRuleSet rules = RecipeRuleParser.parseRuleSet(section);
                if (rules != null) {
                        rulesByKey.put(key, rules);
                }

                boolean added = plugin.getServer().addRecipe(recipe);
                if (!added) {
                        plugin.getLogger().warning("La receta '" + id + "' no se pudo añadir (posible duplicado)."
                                        + " Key=" + key.getKey());
                }
        }


        private ShapedRecipe buildShapedRecipe(NamespacedKey key, ItemStack result, ConfigurationSection section) {
                ShapedRecipe recipe = new ShapedRecipe(key, result);

                var pattern = section.getStringList("pattern");
                if (pattern == null || pattern.isEmpty()) {
                        throw new IllegalArgumentException("shaped requiere 'pattern'");
                }
                recipe.shape(pattern.toArray(String[]::new));

                ConfigurationSection map = section.getConfigurationSection("key");
                if (map == null) {
                        throw new IllegalArgumentException("shaped requiere 'key'");
                }

                for (String k : map.getKeys(false)) {
                        if (k.length() != 1) continue;
                        char symbol = k.charAt(0);
                        RecipeChoice choice = resolveChoice(map.getConfigurationSection(k));
                        if (choice == null) {
                                throw new IllegalArgumentException("ingrediente inválido para clave '" + k + "'");
                        }
                        recipe.setIngredient(symbol, choice);
                }

                return recipe;
        }

        private ShapelessRecipe buildShapelessRecipe(NamespacedKey key, ItemStack result, ConfigurationSection section) {
                ShapelessRecipe recipe = new ShapelessRecipe(key, result);

                var list = section.getMapList("ingredients");
                if (list == null || list.isEmpty()) {
                        throw new IllegalArgumentException("shapeless requiere 'ingredients'");
                }

                for (Map<?, ?> raw : list) {
                        int count = 1;
                        Object maybeCount = raw.get("count");
                        if (maybeCount instanceof Number n) {
                                count = Math.max(1, n.intValue());
                        }

                        RecipeChoice choice = resolveChoice(raw);
                        if (choice == null) {
                                throw new IllegalArgumentException("ingrediente inválido en shapeless");
                        }

                        if (choice instanceof RecipeChoice.MaterialChoice mc) {
                                // ShapelessRecipe en esta API no soporta RecipeChoice directamente.
                                // MaterialChoice siempre es de 1 material (ver resolveChoice).
                                recipe.addIngredient(mc.getChoices().get(0), count);
                                continue;
                        }

                        if (choice instanceof RecipeChoice.ExactChoice ec) {
                                // Preserva meta exacta para ítems custom.
                                ItemStack ingredient = ec.getChoices().get(0).clone();
                                ingredient.setAmount(1);
                                recipe.addIngredient(count, ingredient);
                                continue;
                        }

                        throw new IllegalArgumentException("ingrediente shapeless no soportado: " + choice.getClass().getSimpleName());
                }
                return recipe;
        }

        private FurnaceRecipe buildFurnaceRecipe(NamespacedKey key, ItemStack result, ConfigurationSection section) {
                RecipeChoice input = resolveChoice(section.getConfigurationSection("input"));
                if (input == null) throw new IllegalArgumentException("furnace requiere 'input'");
                float exp = (float) section.getDouble("experience", 0.0);
                int cook = section.getInt("cookingTime", 200);
                return new FurnaceRecipe(key, result, input, exp, cook);
        }

        private BlastingRecipe buildBlastingRecipe(NamespacedKey key, ItemStack result, ConfigurationSection section) {
                RecipeChoice input = resolveChoice(section.getConfigurationSection("input"));
                if (input == null) throw new IllegalArgumentException("blasting requiere 'input'");
                float exp = (float) section.getDouble("experience", 0.0);
                int cook = section.getInt("cookingTime", 100);
                return new BlastingRecipe(key, result, input, exp, cook);
        }

        private SmokingRecipe buildSmokingRecipe(NamespacedKey key, ItemStack result, ConfigurationSection section) {
                RecipeChoice input = resolveChoice(section.getConfigurationSection("input"));
                if (input == null) throw new IllegalArgumentException("smoking requiere 'input'");
                float exp = (float) section.getDouble("experience", 0.0);
                int cook = section.getInt("cookingTime", 100);
                return new SmokingRecipe(key, result, input, exp, cook);
        }

        private CampfireRecipe buildCampfireRecipe(NamespacedKey key, ItemStack result, ConfigurationSection section) {
                RecipeChoice input = resolveChoice(section.getConfigurationSection("input"));
                if (input == null) throw new IllegalArgumentException("campfire requiere 'input'");
                float exp = (float) section.getDouble("experience", 0.0);
                int cook = section.getInt("cookingTime", 600);
                return new CampfireRecipe(key, result, input, exp, cook);
        }

        private StonecuttingRecipe buildStonecuttingRecipe(NamespacedKey key, ItemStack result, ConfigurationSection section) {
                RecipeChoice input = resolveChoice(section.getConfigurationSection("input"));
                if (input == null) throw new IllegalArgumentException("stonecutting requiere 'input'");
                return new StonecuttingRecipe(key, result, input);
        }

        private SmithingTransformRecipe buildSmithingTransformRecipe(NamespacedKey key, ItemStack result, ConfigurationSection section) {
                RecipeChoice template = resolveChoice(section.getConfigurationSection("template"));
                RecipeChoice base = resolveChoice(section.getConfigurationSection("base"));
                RecipeChoice addition = resolveChoice(section.getConfigurationSection("addition"));
                if (template == null || base == null || addition == null) {
                        throw new IllegalArgumentException("smithing_transform requiere template/base/addition");
                }
                return new SmithingTransformRecipe(key, result, template, base, addition);
        }

        private ItemStack resolveResultItem(ConfigurationSection resultSection) {
                if (resultSection == null) return null;

                int amount = Math.max(1, resultSection.getInt("amount", 1));

                String materialName = resultSection.getString("material");
                if (materialName != null && !materialName.isBlank()) {
                        Material material = Material.matchMaterial(materialName.trim().toUpperCase(Locale.ROOT));
                        if (material == null) throw new IllegalArgumentException("material inválido: " + materialName);
                        return new ItemStack(material, amount);
                }

                String customName = resultSection.getString("custom");
                if (customName != null && !customName.isBlank()) {
                        CustomItemType type = CustomItemType.valueOf(customName.trim().toUpperCase(Locale.ROOT));
                        var customItem = plugin.getItemManager().getItem(type);
                        if (customItem == null) throw new IllegalArgumentException("custom no registrado: " + type);

                        // Soporte para resultados parametrizados (p. ej. LOKI_DICE)
                        ConfigurationSection params = resultSection.getConfigurationSection("params");
                        if (customItem instanceof LokiDice dice && params != null && params.contains("luck")) {
                                double luck = params.getDouble("luck");
                                ItemStack out = dice.getItemStack(luck);
                                out.setAmount(amount);
                                return out;
                        }

                        ItemStack out = customItem.getItemStack();
                        out.setAmount(amount);
                        return out;
                }

                return null;
        }

        private RecipeChoice resolveChoice(ConfigurationSection ingredientSection) {
                if (ingredientSection == null) return null;
                return resolveChoice(ingredientSection.getValues(false));
        }

        private RecipeChoice resolveChoice(Map<?, ?> raw) {
                Object materialName = raw.get("material");
                if (materialName instanceof String s && !s.isBlank()) {
                        Material material = Material.matchMaterial(s.trim().toUpperCase(Locale.ROOT));
                        if (material == null) throw new IllegalArgumentException("material inválido: " + s);
                        return new RecipeChoice.MaterialChoice(material);
                }

                Object customName = raw.get("custom");
                if (customName instanceof String s && !s.isBlank()) {
                        CustomItemType type = CustomItemType.valueOf(s.trim().toUpperCase(Locale.ROOT));
                        var customItem = plugin.getItemManager().getItem(type);
                        if (customItem == null) throw new IllegalArgumentException("custom no registrado: " + type);
                        ItemStack ingredient = customItem.getItemStack();
                        ingredient.setAmount(1);
                        return new RecipeChoice.ExactChoice(ingredient);
                }

                return null;
        }
}
