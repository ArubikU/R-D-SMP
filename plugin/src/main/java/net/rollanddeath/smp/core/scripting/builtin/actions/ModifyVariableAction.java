package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rollanddeath.smp.core.scripting.ActionResult;

final class ModifyVariableAction {
    private ModifyVariableAction() {}

    static void register() {
        ActionRegistrar.register("modify_variable", ModifyVariableAction::parse, "modify_var", "var_op");
    }

    private static Action parse(Map<?, ?> raw) {
        String key = Resolvers.string(null, raw, "key", "var", "variable");
        String operation = Resolvers.string(null, raw, "operation", "op", "action");
        Object valueSpec = raw.get("value");
        if (valueSpec == null) valueSpec = raw.get("val");
        if (valueSpec == null) valueSpec = raw.get("amount");

        final String finalKey = key;
        final String finalOperation = operation;
        final Object finalValueSpec = valueSpec;

        return ctx -> {
            if (finalKey == null || finalOperation == null) return ActionResult.ALLOW;

            String op = finalOperation.toLowerCase();
            Object current = ctx.getValue(finalKey);
            Object value = Resolvers.resolve(ctx, finalValueSpec);

            switch (op) {
                case "set", "=" -> {
                    ctx.setGenericVarCompat(finalKey, value);
                }
                case "add", "+", "increment" -> {
                    if (current instanceof Number n1 && value instanceof Number n2) {
                        double result = n1.doubleValue() + n2.doubleValue();
                        if (current instanceof Integer && value instanceof Integer) {
                            ctx.setGenericVarCompat(finalKey, (int) result);
                        } else {
                            ctx.setGenericVarCompat(finalKey, result);
                        }
                    } else if (current instanceof String s) {
                        ctx.setGenericVarCompat(finalKey, s + value);
                    }
                }
                case "sub", "-", "subtract", "decrement" -> {
                    if (current instanceof Number n1 && value instanceof Number n2) {
                        double result = n1.doubleValue() - n2.doubleValue();
                        if (current instanceof Integer && value instanceof Integer) {
                            ctx.setGenericVarCompat(finalKey, (int) result);
                        } else {
                            ctx.setGenericVarCompat(finalKey, result);
                        }
                    }
                }
                case "mul", "*", "multiply" -> {
                    if (current instanceof Number n1 && value instanceof Number n2) {
                        double result = n1.doubleValue() * n2.doubleValue();
                        ctx.setGenericVarCompat(finalKey, result);
                    }
                }
                case "div", "/", "divide" -> {
                    if (current instanceof Number n1 && value instanceof Number n2) {
                        if (n2.doubleValue() == 0) return ActionResult.ALLOW;
                        double result = n1.doubleValue() / n2.doubleValue();
                        ctx.setGenericVarCompat(finalKey, result);
                    }
                }
                case "mod", "%", "modulo" -> {
                    if (current instanceof Number n1 && value instanceof Number n2) {
                        if (n2.doubleValue() == 0) return ActionResult.ALLOW;
                        double result = n1.doubleValue() % n2.doubleValue();
                        ctx.setGenericVarCompat(finalKey, result);
                    }
                }
                case "append", "push", "add_to_list" -> {
                    List<Object> list;
                    if (current instanceof List) {
                        list = new ArrayList<>((List<?>) current);
                    } else {
                        list = new ArrayList<>();
                        if (current != null) list.add(current);
                    }
                    list.add(value);
                    ctx.setGenericVarCompat(finalKey, list);
                }
                case "remove", "pop", "remove_from_list" -> {
                    if (current instanceof List) {
                        List<Object> list = new ArrayList<>((List<?>) current);
                        list.remove(value);
                        ctx.setGenericVarCompat(finalKey, list);
                    }
                }
                case "put", "set_key" -> {
                    Map<Object, Object> map;
                    if (current instanceof Map) {
                        map = new HashMap<>((Map<?, ?>) current);
                    } else {
                        map = new HashMap<>();
                    }
                    // Expecting value to be a map with key/value or just a value if we have a 'map_key' param?
                    // But the action spec only has 'value'.
                    // Maybe the user passed a map as value?
                    if (value instanceof Map<?, ?> m) {
                        map.putAll(m);
                    }
                    ctx.setGenericVarCompat(finalKey, map);
                }
                case "clear", "delete" -> {
                    ctx.setGenericVarCompat(finalKey, null);
                }
            }
            return ActionResult.ALLOW;
        };
    }
}
