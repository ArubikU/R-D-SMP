package net.rollanddeath.smp.core.scripting.scope;

public final class ScriptAccessException extends RuntimeException {

    private final String path;

    public ScriptAccessException(String path, String message) {
        super(message);
        this.path = path;
    }

    public String path() {
        return path;
    }
}
