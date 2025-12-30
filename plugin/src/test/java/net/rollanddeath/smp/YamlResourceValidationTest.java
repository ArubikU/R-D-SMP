package net.rollanddeath.smp;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

public class YamlResourceValidationTest {

    private static final Set<String> TYPES_REQUIRE_SCOPED_KEY = Set.of(
        // Conditions
        "var_truthy",
        "var_is_missing",
        "var_equals",
        "var_in",
        "var_compare",
        "now_ms_gte_var",
        "material_in_tag",
        "material_is_ore",
        "var_matches_regex",
        // Actions
        "set_var",
        "add_var",
        "math_set_var",
        "set_var_now_plus"
    );

    @Test
    void parsesAllYamlResourcesAndHasNoLegacyScopePaths() throws IOException {
        Path resourcesRoot = Paths.get("src", "main", "resources");
        if (!Files.isDirectory(resourcesRoot)) {
            fail("No existe src/main/resources (cwd=" + Paths.get("").toAbsolutePath() + ")");
            return;
        }

        Yaml yaml = new Yaml();
        List<String> errors = new ArrayList<>();

        try (var stream = Files.walk(resourcesRoot)) {
            stream
                .filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".yml"))
                .forEach(p -> {
                    String content;
                    try {
                        content = Files.readString(p, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        errors.add(p + ": no se pudo leer (" + e.getMessage() + ")");
                        return;
                    }

                    Object root;
                    try {
                        root = yaml.load(content);
                    } catch (YAMLException e) {
                        errors.add(p + ": YAML inválido (" + e.getMessage() + ")");
                        return;
                    } catch (Exception e) {
                        errors.add(p + ": error parseando YAML (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")");
                        return;
                    }

                    // Validación:
                    // - no permitir el segmento legacy EVENT.cache.* en ningún string.
                    // - no permitir roots legacy ${caster...}, caster.*, ${target...}, target.*, ${projectile...}, projectile.*
                    // - no permitir keys sin scope (sin punto) en conditions/actions que leen/escriben vars.
                    // - no permitir store_key/store_id_key sin scope (sin punto).
                    scanForLegacyStringsAndUnscopedKeys(p, root, errors);
                });
        }

        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder("Validación YAML falló (" + errors.size() + " problema(s)):\n");
            for (String e : errors) {
                sb.append("- ").append(e).append('\n');
            }
            fail(sb.toString());
        }
    }

    private static void scanForLegacyStringsAndUnscopedKeys(Path file, Object node, List<String> errors) {
        if (node == null) return;

        if (node instanceof String s) {
            String lower = s.toLowerCase();

            if (lower.contains("event.cache")) {
                errors.add(file + ": contiene legacy 'EVENT.cache' en string: " + preview(s));
                return;
            }

            // Solo consideramos patrones de scripting/paths, para evitar falsos positivos en texto normal.
            boolean looksLikeExpression = s.contains("${") || s.contains(".");
            if (looksLikeExpression) {
                if (lower.contains("${caster") || lower.contains("caster.")) {
                    errors.add(file + ": contiene legacy root 'caster' en string: " + preview(s));
                }
                if (lower.contains("${target") || lower.contains("target.")) {
                    errors.add(file + ": contiene legacy root 'target' en string: " + preview(s));
                }
                if (lower.contains("${projectile") || lower.contains("projectile.")) {
                    errors.add(file + ": contiene legacy root 'projectile' en string: " + preview(s));
                }
            }
            return;
        }

        if (node instanceof Map<?, ?> map) {
            validateTypedMapForScopedKeys(file, map, errors);
            for (Map.Entry<?, ?> e : map.entrySet()) {
                scanForLegacyStringsAndUnscopedKeys(file, e.getKey(), errors);
                scanForLegacyStringsAndUnscopedKeys(file, e.getValue(), errors);
            }
            return;
        }

        if (node instanceof Iterable<?> it) {
            for (Object v : it) {
                scanForLegacyStringsAndUnscopedKeys(file, v, errors);
            }
        }
    }

    private static void validateTypedMapForScopedKeys(Path file, Map<?, ?> map, List<String> errors) {
        if (map == null || map.isEmpty()) return;

        Object typeObj = map.get("type");
        String type = (typeObj instanceof String s) ? s.trim().toLowerCase() : null;

        if (type != null && TYPES_REQUIRE_SCOPED_KEY.contains(type)) {
            Object keyObj = map.get("key");
            if (keyObj instanceof String key) {
                String k = key.trim();
                if (!k.isEmpty() && !k.contains(".")) {
                    errors.add(file + ": '" + type + "' usa key sin scope (sin punto): " + preview(key));
                }
            }
        }

        Object storeKeyObj = map.get("store_key");
        if (storeKeyObj instanceof String storeKey) {
            String k = storeKey.trim();
            if (!k.isEmpty() && !k.contains(".")) {
                errors.add(file + ": store_key sin scope (sin punto): " + preview(storeKey));
            }
        }

        Object storeIdKeyObj = map.get("store_id_key");
        if (storeIdKeyObj instanceof String storeIdKey) {
            String k = storeIdKey.trim();
            if (!k.isEmpty() && !k.contains(".")) {
                errors.add(file + ": store_id_key sin scope (sin punto): " + preview(storeIdKey));
            }
        }
    }

    private static String preview(String s) {
        String oneLine = s.replace("\r", " ").replace("\n", " ").trim();
        if (oneLine.length() <= 140) return '"' + oneLine + '"';
        return '"' + oneLine.substring(0, 140) + "…\"";
    }
}
