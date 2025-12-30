package net.rollanddeath.smp.core.scripting.lint;

import java.util.Objects;

public final class ScriptLintIssue {

    private final ScriptLintSeverity severity;
    private final String source;
    private final String path;
    private final String message;
    private final String token;
    private final String suggestion;

    public ScriptLintIssue(
        ScriptLintSeverity severity,
        String source,
        String path,
        String message,
        String token,
        String suggestion
    ) {
        this.severity = Objects.requireNonNull(severity, "severity");
        this.source = source != null ? source : "<unknown>";
        this.path = path != null ? path : "<root>";
        this.message = Objects.requireNonNull(message, "message");
        this.token = token;
        this.suggestion = suggestion;
    }

    public ScriptLintSeverity severity() {
        return severity;
    }

    public String source() {
        return source;
    }

    public String path() {
        return path;
    }

    public String message() {
        return message;
    }

    public String token() {
        return token;
    }

    public String suggestion() {
        return suggestion;
    }

    @Override
    public String toString() {
        String base = severity + " " + source + ":" + path + ": " + message;
        if (token != null && !token.isBlank()) {
            base += " (token='" + token + "')";
        }
        if (suggestion != null && !suggestion.isBlank()) {
            base += " sugerencia='" + suggestion + "'";
        }
        return base;
    }
}
