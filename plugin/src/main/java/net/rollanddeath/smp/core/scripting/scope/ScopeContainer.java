package net.rollanddeath.smp.core.scripting.scope;

/**
 * Representa un scope (PLAYER/WORLD/CHUNK/GLOBAL/TEAM/EVENT) expuesto al sistema de scripting.
 *
 * Diseño:
 * - getters nativos y state: solo lectura
 * - generic/custom: mutable desde scripts (enforcement estricto)
 * - cache/state: solo engine
 */
public interface ScopeContainer {

    ScopeId id();

    Object base();

    Object get(ScopePath path);

    void setGeneric(String rawPath, Object value);

    void setCacheEngineOnly(String rawPath, Object value);

    void setStateEngineOnly(String rawPath, Object value);

    /**
     * Acceso al storage interno para operaciones avanzadas del engine.
     * Usar con cuidado - preferir métodos get/set cuando sea posible.
     */
    ScopeStorage storage();
}
