package net.rollanddeath.smp.core.scripting.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Storage persistente por identidad de scope.
 *
 * - generic: variables custom (mutables por scripts)
 * - cache: solo engine
 * - state: solo engine (y/o snapshots, si el provider lo decide)
 */
public final class ScopeStorage {

    private final Map<String, Object> generic = new ConcurrentHashMap<>();
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private final Map<String, Object> state = new ConcurrentHashMap<>();

    public Map<String, Object> genericRoot() {
        return generic;
    }

    public Map<String, Object> cacheRoot() {
        return cache;
    }

    public Map<String, Object> stateRoot() {
        return state;
    }
}
