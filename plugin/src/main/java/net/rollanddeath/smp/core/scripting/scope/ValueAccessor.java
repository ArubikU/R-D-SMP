package net.rollanddeath.smp.core.scripting.scope;

/**
 * Acceso de solo lectura a un espacio de valores (native/state).
 *
 * Se usa para snapshots o para un acceso reflectivo (read-only).
 */
@FunctionalInterface
interface ValueAccessor {
    Object get(String[] keyPath);
}
