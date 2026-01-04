package net.rollanddeath.smp.core.scripting.lint;

import net.rollanddeath.smp.core.scripting.scope.ScopeId;
import net.rollanddeath.smp.core.scripting.scope.ScopePath;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ScriptLinter {

    private ScriptLinter() {
    }

    private static final Pattern TOKEN_PATTERN_ANY_CASE = Pattern.compile(
        "\\b(?:EVENT|SUBJECT|TARGET|PROJECTILE|ITEM|PLAYER|WORLD|CHUNK|GLOBAL|TEAM|LOCATION)\\.[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)*\\b",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern TOKEN_PATTERN_UPPER = Pattern.compile(
        "\\b(?:EVENT|SUBJECT|TARGET|PROJECTILE|ITEM|PLAYER|WORLD|CHUNK|GLOBAL|TEAM|LOCATION)\\.[A-Za-z0-9_]+(?:\\.[A-Za-z0-9_]+)*\\b"
    );

    private static final Set<String> LINT_KEY_NAMES = Set.of(
        // paths comunes
        "key",
        "other_key",
        "a_key",
        "b_key",
        "amount_key",
        "value_key",
        "store_key",
        "curve_side_key",
        "curve_height_key",
        "radius_key",
        // ubicaciones
        "where",
        "center"
    );

    private static final Set<String> EVENT_BASE_KEYS = Set.of(
        "native",
        "class",
        "simpleClass",
        "location",
        "from",
        "to",
        "damage",
        "finalDamage",
        "cause",
        "entity",
        "player",
        "damager",
        "block",
        "item",
        "hitBlock",
        "hitEntity",
        "hitLocation",
        "delta",
        "type",
        "modifier"
    );

    public static List<ScriptLintIssue> lintConfiguration(String sourceName, ConfigurationSection root) {
        List<ScriptLintIssue> out = new ArrayList<>();
        if (root == null) return out;
        visit(sourceName, "<root>", null, root, out);
        return out;
    }

    /**
     * Variante para tests/validación offline: acepta el árbol parseado (Map/List/String) por SnakeYAML u otros.
     */
    public static List<ScriptLintIssue> lintObject(String sourceName, Object root) {
        List<ScriptLintIssue> out = new ArrayList<>();
        if (root == null) return out;
        visit(sourceName, "<root>", null, root, out);
        return out;
    }

    public static void logIssues(Logger logger, List<ScriptLintIssue> issues) {
        if (logger == null || issues == null || issues.isEmpty()) return;

        long errors = issues.stream().filter(i -> i.severity() == ScriptLintSeverity.ERROR).count();
        long warns = issues.size() - errors;
        logger.warning("ScriptLinter: " + errors + " error(es), " + warns + " warning(s)");

        for (ScriptLintIssue issue : issues) {
            String line = "[ScriptLinter] " + issue;
            if (issue.severity() == ScriptLintSeverity.ERROR) {
                logger.severe(line);
            } else {
                logger.warning(line);
            }
        }
    }

    private static void visit(String sourceName, String path, String keyName, Object node, List<ScriptLintIssue> out) {
        if (node == null) return;

        if (node instanceof ConfigurationSection sec) {
            for (String key : sec.getKeys(false)) {
                Object child = sec.get(key);
                String childPath = path + "." + key;
                visit(sourceName, childPath, key, child, out);
            }
            return;
        }

        if (node instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> e : map.entrySet()) {
                Object kObj = e.getKey();
                if (!(kObj instanceof String k)) continue;
                Object child = e.getValue();
                String childPath = path + "." + k;
                visit(sourceName, childPath, k, child, out);
            }
            return;
        }

        if (node instanceof List<?> list) {
            for (int i = 0; i < list.size(); i++) {
                visit(sourceName, path + "[" + i + "]", keyName, list.get(i), out);
            }
            return;
        }

        if (node instanceof String s) {
            lintStringValue(sourceName, path, keyName, s, out);
        }
    }

    private static void lintStringValue(String sourceName, String path, String keyName, String s, List<ScriptLintIssue> out) {
        if (s == null || s.isBlank()) return;

        if (!shouldLintString(keyName, s)) return;

        Matcher m = TOKEN_PATTERN_ANY_CASE.matcher(s);
        Set<String> seen = new HashSet<>();
        while (m.find()) {
            String token = m.group();
            if (token == null || token.isBlank()) continue;
            if (!seen.add(token)) continue;
            lintToken(sourceName, path, token, out);
        }
    }

    private static void lintToken(String sourceName, String path, String token, List<ScriptLintIssue> out) {
        if (token == null || token.isBlank()) return;

        String normalized = normalizeScopePrefix(token);
        if (!normalized.equals(token)) {
            String rawPrefix = token.substring(0, token.indexOf('.'));
            String expected = rawPrefix.toUpperCase(Locale.ROOT);
            out.add(new ScriptLintIssue(
                ScriptLintSeverity.ERROR,
                sourceName,
                path,
                "Scope debe ir en mayúsculas ('" + expected + ".*')",
                token,
                normalized
            ));
        }

        // A partir de aquí validamos usando el token normalizado
        token = normalized;

        if (token.contains(".__")) {
            out.add(new ScriptLintIssue(
                ScriptLintSeverity.ERROR,
                sourceName,
                path,
                "No se permite acceder a claves internas '__*' desde scripts",
                token,
                null
            ));
            return;
        }

        ScopePath p = ScopePath.parse(token);
        if (p == null) {
            out.add(new ScriptLintIssue(
                ScriptLintSeverity.WARN,
                sourceName,
                path,
                "Path de scope no se pudo parsear (¿typo?)",
                token,
                null
            ));
            return;
        }

        // EVENT.args.* es válido para pasar argumentos a callbacks (on_hit, etc.)
        // Solo advertir si se usa en otros contextos donde no tendría sentido

        // EVENT: chequeos extra para claves base típicas
        if (p.scope() == ScopeId.EVENT && p.kind() == ScopePath.Kind.NATIVE) {
            String[] kp = p.keyPath();
            if (kp != null && kp.length > 0) {
                String first = kp[0];
                if ("__native".equalsIgnoreCase(first) || "__event".equalsIgnoreCase(first)) {
                    out.add(new ScriptLintIssue(
                        ScriptLintSeverity.ERROR,
                        sourceName,
                        path,
                        "No usar EVENT.__native/EVENT.__event; usa EVENT.native.*",
                        token,
                        "EVENT.native.*"
                    ));
                    return;
                }

                // Sugerencias comunes (snake_case en claves base del payload)
                String suggested = suggestEventBaseKey(first);
                if (suggested != null) {
                    out.add(new ScriptLintIssue(
                        ScriptLintSeverity.WARN,
                        sourceName,
                        path,
                        "Clave EVENT.* sospechosa; en payload base se usa '" + suggested + "'",
                        token,
                        token.replaceFirst(Pattern.quote("EVENT." + first), "EVENT." + suggested)
                    ));
                    return;
                }

                // Si parece una clave base con typo leve, avisar.
                if (!EVENT_BASE_KEYS.contains(first) && looksLikeBaseEventKey(first)) {
                    String nearest = nearest(first, EVENT_BASE_KEYS);
                    if (nearest != null) {
                        out.add(new ScriptLintIssue(
                            ScriptLintSeverity.WARN,
                            sourceName,
                            path,
                            "Posible typo en EVENT.*",
                            token,
                            token.replaceFirst(Pattern.quote("EVENT." + first), "EVENT." + nearest)
                        ));
                    }
                }
            }
        }
    }

    private static boolean shouldLintString(String keyName, String value) {
        if (value == null || value.isBlank()) return false;

        // Si el string tiene tokens en MAYÚSCULAS (EVENT./SUBJECT./etc), siempre lint.
        Matcher upper = TOKEN_PATTERN_UPPER.matcher(value);
        if (upper.find()) return true;

        // Si es plantilla, lint también (podría contener ${event.foo}).
        if (value.contains("${")) return true;

        if (keyName == null || keyName.isBlank()) return false;
        String k = keyName.trim().toLowerCase(Locale.ROOT);

        // keys exactas
        if (LINT_KEY_NAMES.contains(k)) return true;

        // convención: cualquier cosa *_key suele ser un path
        if (k.endsWith("_key")) return true;

        // Algunos campos aceptan directamente un scope-path
        if (k.equals("size") || k.equals("material_key") || k.equals("world_key") || k.equals("location_key")) return true;

        return false;
    }

    private static String normalizeScopePrefix(String token) {
        int dot = token.indexOf('.');
        if (dot <= 0) return token;

        String prefix = token.substring(0, dot);
        String expected = prefix.toUpperCase(Locale.ROOT);

        // Solo normalizar si el prefijo es un scope conocido (case-insensitive)
        return switch (expected) {
            case "EVENT", "SUBJECT", "TARGET", "PROJECTILE", "ITEM", "PLAYER", "WORLD", "CHUNK", "GLOBAL", "TEAM", "LOCATION" ->
                expected + token.substring(dot);
            default -> token;
        };
    }

    private static boolean looksLikeBaseEventKey(String firstSegment) {
        if (firstSegment == null) return false;
        String f = firstSegment.toLowerCase(Locale.ROOT);
        return f.contains("damage") || f.contains("location") || f.contains("class") || f.equals("delta");
    }

    private static String suggestEventBaseKey(String firstSegment) {
        if (firstSegment == null) return null;
        String f = firstSegment.trim();
        if (f.isEmpty()) return null;

        String low = f.toLowerCase(Locale.ROOT);
        return switch (low) {
            case "final_damage" -> "finalDamage";
            case "simple_class" -> "simpleClass";
            case "hit_location" -> "hitLocation";
            case "hit_block" -> "hitBlock";
            case "hit_entity" -> "hitEntity";
            default -> null;
        };
    }

    private static String nearest(String raw, Set<String> options) {
        if (raw == null || options == null || options.isEmpty()) return null;
        String needle = raw.toLowerCase(Locale.ROOT);

        String best = null;
        int bestScore = Integer.MAX_VALUE;
        for (String opt : options) {
            if (opt == null) continue;
            int d = editDistance(needle, opt.toLowerCase(Locale.ROOT));
            if (d < bestScore) {
                bestScore = d;
                best = opt;
            }
        }
        // Umbral conservador: evitar sugerir cosas random
        return bestScore <= 3 ? best : null;
    }

    private static int editDistance(String a, String b) {
        if (a == null) return b == null ? 0 : b.length();
        if (b == null) return a.length();

        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[a.length()][b.length()];
    }
}
