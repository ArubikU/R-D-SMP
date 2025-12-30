package net.rollanddeath.smp.core.scripting.library;

import java.util.HashSet;
import java.util.Set;

/**
 * Guard simple para evitar recursión/ciclos al usar scripts reutilizables (call).
 */
public final class CallGuard {

    private static final ThreadLocal<Set<String>> ACTIVE = ThreadLocal.withInitial(HashSet::new);

    private CallGuard() {
    }

    public static boolean enter(String key) {
        if (key == null) return true;
        Set<String> set = ACTIVE.get();
        if (set.contains(key)) return false;
        set.add(key);
        return true;
    }

    public static void exit(String key) {
        if (key == null) return;
        ACTIVE.get().remove(key);
    }
}
