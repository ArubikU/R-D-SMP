package net.rollanddeath.smp.core.scripting.builtin;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.ModifierManager;
import net.rollanddeath.smp.core.roles.RoleType;
import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.integration.PlaceholderUtil;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class BuiltInConditions {

    private BuiltInConditions() {
    }

    public static Condition parse(Map<?, ?> raw) {
        String type = getString(raw, "type");
        if (type == null) return null;
        type = type.trim().toLowerCase(Locale.ROOT);

        return switch (type) {
            case "min_day" -> {
                Integer value = getInt(raw, "value");
                if (value == null) yield null;
                yield ctx -> {
                    RollAndDeathSMP p = ctx.plugin();
                    int currentDay = Math.max(1, p.getGameManager() != null ? p.getGameManager().getCurrentDay() : 1);
                    return currentDay >= value;
                };
            }
            case "lives_at_least" -> {
                Integer value = getInt(raw, "value");
                if (value == null) yield null;
                yield ctx -> ctx.plugin().getLifeManager().getLives(ctx.player()) >= value;
            }
            case "role_is" -> {
                String value = getString(raw, "value");
                if (value == null || value.isBlank()) yield null;
                RoleType required;
                try {
                    required = RoleType.valueOf(value.trim().toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    yield null;
                }
                yield ctx -> ctx.plugin().getRoleManager() != null
                    && Objects.equals(ctx.plugin().getRoleManager().getPlayerRole(ctx.player()), required);
            }
            case "modifier_active" -> {
                String name = getString(raw, "name");
                if (name == null || name.isBlank()) yield null;
                yield ctx -> {
                    ModifierManager mm = ctx.plugin().getModifierManager();
                    return mm != null && mm.isActive(name);
                };
            }
            case "placeholder_compare" -> {
                String placeholder = getString(raw, "placeholder");
                String operator = Optional.ofNullable(getString(raw, "operator")).map(String::trim).orElse("==");
                String expected = getString(raw, "value");
                if (placeholder == null || placeholder.isBlank() || expected == null) yield null;

                boolean caseInsensitive = raw.get("case_insensitive") instanceof Boolean b ? b : true;

                yield ctx -> {
                    RollAndDeathSMP p = ctx.plugin();
                    Player player = ctx.player();

                    String actual = PlaceholderUtil.resolvePlaceholders(p, player, placeholder);
                    String exp = PlaceholderUtil.resolvePlaceholders(p, player, expected);
                    if (actual == null) actual = "";
                    if (exp == null) exp = "";

                    String a = caseInsensitive ? actual.toLowerCase(Locale.ROOT) : actual;
                    String e = caseInsensitive ? exp.toLowerCase(Locale.ROOT) : exp;

                    Double an = tryParseDouble(a);
                    Double en = tryParseDouble(e);

                    if (an != null && en != null && isNumericOp(operator)) {
                        return switch (operator) {
                            case ">" -> an > en;
                            case ">=" -> an >= en;
                            case "<" -> an < en;
                            case "<=" -> an <= en;
                            case "==" -> Double.compare(an, en) == 0;
                            case "!=" -> Double.compare(an, en) != 0;
                            default -> false;
                        };
                    }

                    return switch (operator) {
                        case "==" -> a.equals(e);
                        case "!=" -> !a.equals(e);
                        case "contains" -> a.contains(e);
                        default -> false;
                    };
                };
            }
            case "var_equals" -> {
                String key = getString(raw, "key");
                String value = getString(raw, "value");
                if (key == null || key.isBlank() || value == null) yield null;
                boolean caseInsensitive = raw.get("case_insensitive") instanceof Boolean b ? b : true;

                yield ctx -> {
                    String actual = ctx.stringVar(key);
                    if (actual == null) actual = "";
                    String expected = value;

                    if (caseInsensitive) {
                        actual = actual.toLowerCase(Locale.ROOT);
                        expected = expected.toLowerCase(Locale.ROOT);
                    }

                    return actual.equals(expected);
                };
            }
            case "var_in" -> {
                String key = getString(raw, "key");
                Object valuesObj = raw.get("values");
                if (key == null || key.isBlank() || !(valuesObj instanceof List<?> values) || values.isEmpty()) yield null;
                boolean caseInsensitive = raw.get("case_insensitive") instanceof Boolean b ? b : true;

                yield ctx -> {
                    String actual = ctx.stringVar(key);
                    if (actual == null) actual = "";
                    if (caseInsensitive) {
                        actual = actual.toLowerCase(Locale.ROOT);
                    }

                    for (Object o : values) {
                        if (!(o instanceof String s)) continue;
                        String v = caseInsensitive ? s.toLowerCase(Locale.ROOT) : s;
                        if (actual.equals(v)) return true;
                    }
                    return false;
                };
            }
            default -> null;
        };
    }

    private static boolean isNumericOp(String op) {
        return ">".equals(op) || ">=".equals(op) || "<".equals(op) || "<=".equals(op) || "==".equals(op) || "!=".equals(op);
    }

    private static Double tryParseDouble(String s) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String getString(Map<?, ?> raw, String key) {
        Object v = raw.get(key);
        return (v instanceof String s) ? s : null;
    }

    private static Integer getInt(Map<?, ?> raw, String key) {
        Object v = raw.get(key);
        if (v instanceof Number n) return n.intValue();
        if (v instanceof String s) {
            try {
                return Integer.parseInt(s.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
