package net.rollanddeath.smp.core.scripting.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class NestedMaps {

    private NestedMaps() {
    }

    static Object get(Map<String, Object> root, String[] path) {
        if (root == null || path == null || path.length == 0) return null;

        Object current = root;
        for (String segment : path) {
            if (segment == null) return null;
            current = ObjectKeyAccess.getChild(current, segment);
            if (current == null) return null;
        }
        return current;
    }

    static void set(Map<String, Object> root, String[] path, Object value) {
        if (root == null) throw new IllegalArgumentException("root null");
        if (path == null || path.length == 0) throw new IllegalArgumentException("path vacío");

        Map<String, Object> current = root;
        for (int i = 0; i < path.length - 1; i++) {
            String key = path[i];
            Object next = current.get(key);
            if (next == null) {
                Map<String, Object> created = new ConcurrentHashMap<>();
                current.put(key, created);
                current = created;
                continue;
            }
            if (next instanceof Map<?, ?> m) {
                //noinspection unchecked
                current = (Map<String, Object>) m;
                continue;
            }
            // Si el path colisiona con un valor no-mapa, lo reemplazamos por mapa.
            Map<String, Object> created = new ConcurrentHashMap<>();
            current.put(key, created);
            current = created;
        }

        current.put(path[path.length - 1], value);
    }
}
