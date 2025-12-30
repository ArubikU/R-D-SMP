package net.rollanddeath.smp.core.scripting.scope;

import java.util.Map;

final class MapValueAccessor implements ValueAccessor {

    private final Map<String, Object> root;

    MapValueAccessor(Map<String, Object> root) {
        this.root = root;
    }

    @Override
    public Object get(String[] keyPath) {
        return NestedMaps.get(root, keyPath);
    }
}
