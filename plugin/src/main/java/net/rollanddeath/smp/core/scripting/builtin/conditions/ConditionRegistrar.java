package net.rollanddeath.smp.core.scripting.builtin.conditions;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.rollanddeath.smp.core.scripting.Condition;

public final class ConditionRegistrar {
    private static final Map<String, Function<Map<?, ?>, Condition>> REGISTERED = new ConcurrentHashMap<>();

    private ConditionRegistrar() {
    }

    public static void registerDefaults() {
        // Logic flow
        AnyOfCondition.register();
        AllOfCondition.register();
        NotCondition.register();
        RandomChanceCondition.register();
        CallCondition.register();

        // Variables
        VarTruthyCondition.register();
        VarIsMissingCondition.register();
        VarEqualsCondition.register();
        VarInCondition.register();
        VarCompareCondition.register();
        VarMatchesRegexCondition.register();
        NowMsGteVarCondition.register();

        // Entity / Player
        GameModeCondition.register();
        InWaterCondition.register();
        HasCooldownCondition.register();
        InventoryFullCondition.register();
        HasPermissionCondition.register();
        LivesAtLeastCondition.register();
        RoleIsCondition.register();
        
        // World / Environment
        TimeBetweenCondition.register();
        HasStormCondition.register();
        IsThunderingCondition.register();
        EnvironmentCondition.register();
        SkyLightCondition.register();
        MinDayCondition.register();

        // Material / Items
        ObjectTagCondition.register();
        
        // Misc
        ModifierActiveCondition.register();
        PlaceholderCompareCondition.register();
    }

    public static void register(String name, Function<Map<?, ?>, Condition> factory, String... aliases) {
        if (name == null || name.isBlank() || factory == null) return;
        
        String t = name.trim().toLowerCase(Locale.ROOT);
        REGISTERED.put(t, factory);
        if (aliases != null) {
            for (String a : aliases) {
                if (a == null || a.isBlank()) continue;
                REGISTERED.put(a.trim().toLowerCase(Locale.ROOT), factory);
            }
        }
    }

    public static Condition parse(Map<?, ?> raw) {
        if (raw == null) return null;
        Object typeObj = raw.get("type");
        // Fallback for legacy format where type might be inferred or different key?
        // BuiltInConditions used BuiltInArgs.lowerType(raw) which checked "condition", "type", etc.
        // Let's stick to "type" or "condition" for now.
        if (typeObj == null) typeObj = raw.get("condition");
        
        if (typeObj == null) return null;
        String type = String.valueOf(typeObj).trim().toLowerCase(Locale.ROOT);
        
        Function<Map<?, ?>, Condition> factory = REGISTERED.get(type);
        if (factory != null) {
            return factory.apply(raw);
        }
        return null;
    }
}
