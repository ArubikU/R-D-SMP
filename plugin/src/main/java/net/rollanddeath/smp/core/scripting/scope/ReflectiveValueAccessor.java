package net.rollanddeath.smp.core.scripting.scope;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ValueAccessor reflectivo (READ-ONLY).
 *
 * Objetivo: permitir paths tipo EVENT.native.entity.location.x sin necesidad
 * de copiar todo a vars en cada handler.
 *
 * Reglas:
 * - Primero intenta ObjectKeyAccess (Location/Entity/World/etc)
 * - Si no, intenta Map
 * - Si no, intenta getters públicos sin args: getX()/isX()
 * - Enums se convierten a name() para facilitar comparaciones en YAML
 */
final class ReflectiveValueAccessor implements ValueAccessor {

    private static final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, Method>> GETTERS = new ConcurrentHashMap<>();

    private final Object base;

    ReflectiveValueAccessor(Object base) {
        this.base = base;
    }

    @Override
    public Object get(String[] keyPath) {
        if (base == null || keyPath == null || keyPath.length == 0) return null;

        Object current = base;
        for (String rawKey : keyPath) {
            if (current == null) return null;
            if (rawKey == null) return null;
            String key = rawKey.trim();
            if (key.isEmpty()) return null;

            Object child = ObjectKeyAccess.getChild(current, key);
            if (child != null) {
                current = child;
                continue;
            }

            if (current instanceof Map<?, ?> m) {
                current = m.get(key);
                continue;
            }

            Object viaGetter = getViaGetter(current, key);
            if (viaGetter != null) {
                current = viaGetter;
                continue;
            }

            return null;
        }

        if (current instanceof Enum<?> e) {
            return e.name();
        }
        return current;
    }

    private static Object getViaGetter(Object obj, String key) {
        Class<?> cls = obj.getClass();
        Method m = findGetter(cls, key);
        if (m == null) return null;
        try {
            Object v = m.invoke(obj);
            if (v instanceof Enum<?> e) return e.name();
            return v;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Method findGetter(Class<?> cls, String rawKey) {
        if (cls == null || rawKey == null) return null;

        String normalized = normalizeKey(rawKey);
        if (normalized.isBlank()) return null;

        ConcurrentHashMap<String, Method> cache = GETTERS.computeIfAbsent(cls, ignored -> new ConcurrentHashMap<>());
        Method cached = cache.get(normalized);
        if (cached != null) return cached;

        String camel = toCamel(normalized);
        String getName = "get" + camel;
        String isName = "is" + camel;
        String directName = camel.isEmpty() ? "" : Character.toLowerCase(camel.charAt(0)) + camel.substring(1);

        Method found = null;
        try {
            found = cls.getMethod(getName);
        } catch (NoSuchMethodException ignored) {
        }
        if (found == null) {
            try {
                found = cls.getMethod(isName);
            } catch (NoSuchMethodException ignored) {
            }
        }

        // Algunos eventos de Bukkit exponen métodos sin el prefijo get/is, p.ej. blockList().
        if (found == null && !directName.isBlank()) {
            try {
                found = cls.getMethod(directName);
            } catch (NoSuchMethodException ignored) {
            }
        }

        if (found != null && found.getParameterCount() == 0) {
            cache.put(normalized, found);
            return found;
        }

        return null;
    }

    private static String normalizeKey(String key) {
        String k = key.trim().toLowerCase(Locale.ROOT);
        // Permitimos tanto foo_bar como foo-bar como foo bar
        k = k.replace('-', '_').replace(' ', '_');
        while (k.contains("__")) k = k.replace("__", "_");
        return k;
    }

    private static String toCamel(String normalizedUnderscore) {
        if (normalizedUnderscore == null || normalizedUnderscore.isBlank()) return "";
        String[] parts = normalizedUnderscore.split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p == null || p.isBlank()) continue;
            String t = p.trim();
            sb.append(Character.toUpperCase(t.charAt(0)));
            if (t.length() > 1) sb.append(t.substring(1));
        }
        return sb.toString();
    }
}
