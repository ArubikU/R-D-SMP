package net.rollanddeath.smp.core.scripting.scope;

import java.util.Locale;

public final class ScopePath {

    public enum Kind {
        NATIVE,
        GENERIC,
        CACHE,
        STATE
    }

    private final ScopeId scope;
    private final Kind kind;
    private final String[] keyPath; // remaining segments after scope + kind

    private ScopePath(ScopeId scope, Kind kind, String[] keyPath) {
        this.scope = scope;
        this.kind = kind;
        this.keyPath = keyPath;
    }

    public ScopeId scope() {
        return scope;
    }

    public Kind kind() {
        return kind;
    }

    public String[] keyPath() {
        return keyPath;
    }

    public static ScopePath parse(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;

        String[] parts = s.split("\\.");
        if (parts.length < 2) return null;

        ScopeId scope;
        try {
            scope = ScopeId.valueOf(parts[0].trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }

        String second = parts[1].trim().toLowerCase(Locale.ROOT);
        Kind kind;
        int startIndex;
        if ("custom".equals(second) || "generic".equals(second)) {
            kind = Kind.GENERIC;
            startIndex = 2;
        } else if ("cache".equals(second)) {
            kind = Kind.CACHE;
            startIndex = 2;
        } else if ("state".equals(second)) {
            kind = Kind.STATE;
            startIndex = 2;
        } else {
            kind = Kind.NATIVE;
            startIndex = 1;
        }

        if (startIndex >= parts.length) {
            return null;
        }

        String[] keyPath = new String[parts.length - startIndex];
        for (int i = startIndex; i < parts.length; i++) {
            keyPath[i - startIndex] = parts[i];
        }

        return new ScopePath(scope, kind, keyPath);
    }
}
