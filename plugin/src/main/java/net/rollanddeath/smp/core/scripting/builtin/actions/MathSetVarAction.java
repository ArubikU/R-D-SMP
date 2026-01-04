package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;

final class MathSetVarAction {
    private MathSetVarAction() {}

    static void register() {
        ActionRegistrar.register("math_set_var", MathSetVarAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String key = Resolvers.string(null, raw, "key");
        String opRaw = Resolvers.string(null, raw, "operation");
        if (opRaw == null) opRaw = Resolvers.string(null, raw, "op");
        if (key == null || key.isBlank() || opRaw == null || opRaw.isBlank()) return null;
        final String op = opRaw;

        Double a = Resolvers.doubleVal(null, raw, "a");
        String aKey = Resolvers.string(null, raw, "a_key");
        Double b = Resolvers.doubleVal(null, raw, "b");
        String bKey = Resolvers.string(null, raw, "b_key");
        Double c = Resolvers.doubleVal(null, raw, "c");
        String cKey = Resolvers.string(null, raw, "c_key");

        if (a == null && (aKey == null || aKey.isBlank())) return null;

        return ctx -> {
            double valA = resolve(ctx, a, aKey);
            double valB = resolve(ctx, b, bKey);
            double valC = resolve(ctx, c, cKey);
            
            double result = 0;
            switch (op.toLowerCase()) {
                case "add", "+" -> result = valA + valB;
                case "sub", "-" -> result = valA - valB;
                case "mul", "*" -> result = valA * valB;
                case "div", "/" -> result = valB != 0 ? valA / valB : 0;
                case "mod", "%" -> result = valB != 0 ? valA % valB : 0;
                case "pow", "^" -> result = Math.pow(valA, valB);
                case "sqrt" -> result = Math.sqrt(valA);
                case "abs" -> result = Math.abs(valA);
                case "sin" -> result = Math.sin(valA);
                case "cos" -> result = Math.cos(valA);
                case "tan" -> result = Math.tan(valA);
                case "min" -> result = Math.min(valA, valB);
                case "max" -> result = Math.max(valA, valB);
                case "clamp" -> result = Math.max(valB, Math.min(valC, valA));
                case "random" -> result = java.util.concurrent.ThreadLocalRandom.current().nextDouble(valA, valB);
                case "round" -> result = Math.round(valA);
                case "floor" -> result = Math.floor(valA);
                case "ceil" -> result = Math.ceil(valA);
                default -> { return ActionResult.ALLOW; }
            }
            
            ctx.setGenericVarCompat(key, result);
            return ActionResult.ALLOW;
        };
    }

    private static double resolve(net.rollanddeath.smp.core.scripting.ScriptContext ctx, Double val, String key) {
        if (val != null) return val;
        if (key != null) {
            Double d = Resolvers.resolveDouble(ctx.getValue(key));
            if (d != null) return d;
        }
        return 0.0;
    }
}
