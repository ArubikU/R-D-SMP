package net.rollanddeath.smp.core.scripting.scope;

import java.util.Objects;

/**
 * Implementación por defecto de ScopeContainer.
 *
 * - generic/cache/state se respaldan en ScopeStorage
 * - native/state pueden venir de un accessor snapshot o reflectivo
 */
final class DefaultScopeContainer implements ScopeContainer {

    private final ScopeId id;
    private final Object base;
    private final ScopeStorage storage;

    private final ValueAccessor nativeAccessor;
    private final ValueAccessor stateAccessor;

    DefaultScopeContainer(ScopeId id, Object base, ScopeStorage storage, ValueAccessor nativeAccessor, ValueAccessor stateAccessor) {
        this.id = Objects.requireNonNull(id, "id");
        this.base = base;
        this.storage = Objects.requireNonNull(storage, "storage");
        this.nativeAccessor = nativeAccessor;
        this.stateAccessor = stateAccessor;
    }

    @Override
    public ScopeId id() {
        return id;
    }

    @Override
    public Object base() {
        return base;
    }

    @Override
    public ScopeStorage storage() {
        return storage;
    }

    @Override
    public Object get(ScopePath path) {
        if (path == null) return null;
        return switch (path.kind()) {
            case GENERIC -> NestedMaps.get(storage.genericRoot(), path.keyPath());
            case CACHE -> NestedMaps.get(storage.cacheRoot(), path.keyPath());
            case STATE -> {
                Object v = stateAccessor != null ? stateAccessor.get(path.keyPath()) : null;
                if (v != null) yield v;
                yield NestedMaps.get(storage.stateRoot(), path.keyPath());
            }
            case NATIVE -> nativeAccessor != null ? nativeAccessor.get(path.keyPath()) : null;
        };
    }

    @Override
    public void setGeneric(String rawPath, Object value) {
        ScopePath p = ScopePath.parse(rawPath);
        if (p == null) throw new ScriptAccessException(rawPath, "Path inválido: " + rawPath);
        if (p.scope() != id) throw new ScriptAccessException(rawPath, "Scope incorrecto: se esperaba " + id);
        if (p.kind() != ScopePath.Kind.GENERIC) {
            throw new ScriptAccessException(rawPath, "No se puede escribir en '" + p.kind().name().toLowerCase() + "'. Solo se permite escribir en <SCOPE>.custom.* o <SCOPE>.generic.*");
        }
        NestedMaps.set(storage.genericRoot(), p.keyPath(), value);
    }

    @Override
    public void setCacheEngineOnly(String rawPath, Object value) {
        ScopePath p = ScopePath.parse(rawPath);
        if (p == null) throw new ScriptAccessException(rawPath, "Path inválido: " + rawPath);
        if (p.scope() != id) throw new ScriptAccessException(rawPath, "Scope incorrecto: se esperaba " + id);
        if (p.kind() != ScopePath.Kind.CACHE) {
            throw new ScriptAccessException(rawPath, "Solo el engine puede escribir en cache usando <SCOPE>.cache.*");
        }
        NestedMaps.set(storage.cacheRoot(), p.keyPath(), value);
    }

    @Override
    public void setStateEngineOnly(String rawPath, Object value) {
        ScopePath p = ScopePath.parse(rawPath);
        if (p == null) throw new ScriptAccessException(rawPath, "Path inválido: " + rawPath);
        if (p.scope() != id) throw new ScriptAccessException(rawPath, "Scope incorrecto: se esperaba " + id);
        if (p.kind() != ScopePath.Kind.STATE) {
            throw new ScriptAccessException(rawPath, "Solo el engine puede escribir en state usando <SCOPE>.state.*");
        }
        NestedMaps.set(storage.stateRoot(), p.keyPath(), value);
    }
}
