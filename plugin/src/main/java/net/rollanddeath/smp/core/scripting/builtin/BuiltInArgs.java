package net.rollanddeath.smp.core.scripting.builtin;

import net.rollanddeath.smp.core.scripting.ScriptContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Helpers pequeños para parsear args de YAML en builtins.
 *
 * Nota: esto es solo utilitario (no define API de scripting).
 */
public final class BuiltInArgs {

    private BuiltInArgs() {
    }

    public static String string(Map<?, ?> raw, String key) {
        if (raw == null || key == null) return null;
        Object v = raw.get(key);
        return (v instanceof String s) ? s : null;
    }

    public static Integer intValue(Map<?, ?> raw, String key) {
        if (raw == null || key == null) return null;
        Object v = raw.get(key);
        if (v instanceof Number n) return n.intValue();
        if (v instanceof String s) {
            try {
                return Integer.parseInt(s.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    public static Double doubleValue(Map<?, ?> raw, String key) {
        if (raw == null || key == null) return null;
        Object v = raw.get(key);
        if (v instanceof Number n) return n.doubleValue();
        if (v instanceof String s) {
            try {
                return Double.parseDouble(s.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    public static boolean bool(Map<?, ?> raw, String key, boolean defaultValue) {
        if (raw == null || key == null) return defaultValue;
        Object v = raw.get(key);
        return (v instanceof Boolean b) ? b : defaultValue;
    }

    public static boolean invert(Map<?, ?> raw) {
        return bool(raw, "invert", false);
    }

    /**
     * Lee un boolean esperado soportando los nombres "value" y "expected".
     */
    public static boolean expectedBool(Map<?, ?> raw, boolean defaultValue) {
        if (raw == null) return defaultValue;
        Object v = raw.get("value");
        if (v instanceof Boolean b) return b;
        Object e = raw.get("expected");
        if (e instanceof Boolean b) return b;
        return defaultValue;
    }

    /**
     * Soporta convención: ref o id.
     */
    public static String refOrId(Map<?, ?> raw) {
        String ref = string(raw, "ref");
        if (ref == null || ref.isBlank()) ref = string(raw, "id");
        if (ref == null) return null;
        String trimmed = ref.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    /**
     * Parsea un objeto que viene de YAML (Map) a Map<String, Object>, filtrando keys inválidas.
     */
    public static Map<String, Object> stringObjectMap(Object maybeMap) {
        if (!(maybeMap instanceof Map<?, ?> m) || m.isEmpty()) return null;
        Map<String, Object> out = new HashMap<>();
        for (Map.Entry<?, ?> e : m.entrySet()) {
            if (!(e.getKey() instanceof String k) || k.isBlank()) continue;
            out.put(k, e.getValue());
        }
        return out.isEmpty() ? null : out;
    }

    public static void applyWithArgs(ScriptContext ctx, Map<String, Object> with) {
        if (ctx == null || with == null || with.isEmpty()) return;
        for (Map.Entry<String, Object> e : with.entrySet()) {
            if (e.getKey() == null || e.getKey().isBlank()) continue;
            String key = e.getKey().trim();
            if (key.isBlank()) continue;
            try {
                ctx.putCacheVarEngineOnly("EVENT.args." + key, e.getValue());
            } catch (Exception ignored) {
                // fail-safe: args son opcionales
            }
        }
    }

    public static String lowerType(Map<?, ?> raw) {
        String type = string(raw, "type");
        if (type == null) return null;
        return type.trim().toLowerCase(Locale.ROOT);
    }
}
