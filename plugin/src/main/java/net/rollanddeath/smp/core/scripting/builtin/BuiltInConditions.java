package net.rollanddeath.smp.core.scripting.builtin;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.builtin.conditions.ConditionRegistrar;

import java.util.Map;
import java.util.function.Function;

/**
 * Legacy class for backward compatibility.
 * Delegates to {@link ConditionRegistrar}.
 * @deprecated Use {@link ConditionRegistrar} instead.
 */
@Deprecated
public final class BuiltInConditions {

    private BuiltInConditions() {
    }

    public static void register(String type, Function<Map<?, ?>, Condition> parser, String... aliases) {
        ConditionRegistrar.register(type, parser, aliases);
    }

    public static Condition parse(Map<?, ?> raw) {
        return ConditionRegistrar.parse(raw);
    }
}
