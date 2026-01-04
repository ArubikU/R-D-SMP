package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;

import java.util.Locale;
import java.util.Map;

public class VarCompareCondition implements Condition {

    private final String key;
    private final String operator;
    private final Object valueSpec;
    private final String otherKey;
    private final boolean caseInsensitive;

    public static void register() {
        ConditionRegistrar.register("var_compare", VarCompareCondition::new);
    }

    public VarCompareCondition(Map<?, ?> spec) {
        this.key = Resolvers.string(null, spec, "key", "var");
        this.operator = Resolvers.string(null, spec, "operator", "op");
        this.valueSpec = spec.get("value");
        this.otherKey = Resolvers.string(null, spec, "other_key", "var2", "value_key");
        this.caseInsensitive = Resolvers.bool(null, spec.get("case_insensitive")) != Boolean.FALSE;
    }

    @Override
    public boolean test(ScriptContext ctx) {
        if (key == null) return false;
        Object left = ctx.getValue(key);
        Object right = otherKey != null ? ctx.getValue(otherKey) : Resolvers.resolve(ctx, valueSpec);

        boolean result = compare(left, right, operator, caseInsensitive);
        
        // Debug log for args comparisons
        if (key != null && key.contains("args")) {
            if (ctx.plugin() != null) {
                ctx.plugin().getLogger().info("[DEBUG] var_compare: " + key + "=" + left + " " + operator + " " + (otherKey != null ? otherKey + "=" : "") + right + " -> " + result);
            }
        }
        
        return result;
    }

    public static boolean compare(Object left, Object right, String op, boolean caseInsensitive) {
        String operator = op != null ? op.trim() : "==";

        if (left instanceof Number n1 && right instanceof Number n2) {
            double d1 = n1.doubleValue();
            double d2 = n2.doubleValue();
            return switch (operator) {
                case ">" -> d1 > d2;
                case ">=" -> d1 >= d2;
                case "<" -> d1 < d2;
                case "<=" -> d1 <= d2;
                case "==" -> Double.compare(d1, d2) == 0;
                case "!=" -> Double.compare(d1, d2) != 0;
                default -> false;
            };
        }

        String s1 = String.valueOf(left);
        String s2 = String.valueOf(right);

        if (caseInsensitive) {
            s1 = s1.toLowerCase(Locale.ROOT);
            s2 = s2.toLowerCase(Locale.ROOT);
        }

        // Try numeric comparison on strings if they look like numbers
        try {
            double d1 = Double.parseDouble(s1);
            double d2 = Double.parseDouble(s2);
            return switch (operator) {
                case ">" -> d1 > d2;
                case ">=" -> d1 >= d2;
                case "<" -> d1 < d2;
                case "<=" -> d1 <= d2;
                case "==" -> Double.compare(d1, d2) == 0;
                case "!=" -> Double.compare(d1, d2) != 0;
                default -> false;
            };
        } catch (NumberFormatException ignored) {
        }

        return switch (operator) {
            case "==" -> s1.equals(s2);
            case "!=" -> !s1.equals(s2);
            case "contains" -> s1.contains(s2);
            case "starts_with" -> s1.startsWith(s2);
            case "ends_with" -> s1.endsWith(s2);
            default -> false;
        };
    }
}
