package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;

final class SetRandomVarAction {
    private SetRandomVarAction() {}

    static void register() {
        ActionRegistrar.register("set_random_var", SetRandomVarAction::parse, "select_random", "random_var", "random_bool_to_var", "random_boolean_to_var");
    }

    private static Action parse(Map<?, ?> raw) {
        String varName = Resolvers.string(null, raw, "var", "variable", "key", "store", "to", "out", "store_key");
        if (varName == null || varName.isBlank()) return null;

        Object optionsSpec = raw.get("options");
        if (optionsSpec == null) optionsSpec = raw.get("values");
        if (optionsSpec == null) optionsSpec = raw.get("list");
        
        Double probSpec = Resolvers.doubleVal(null, raw, "probability", "p", "chance");
        
        // If no options but probability exists, it's a boolean roll
        boolean isBooleanRoll = (optionsSpec == null && probSpec != null);
        
        final Object finalOptionsSpec = optionsSpec;
        final Double finalProb = probSpec;

        return ctx -> {
            Object result = null;
            
            if (isBooleanRoll) {
                double p = finalProb != null ? finalProb : 0.5;
                result = ThreadLocalRandom.current().nextDouble() < p;
            } else {
                List<WeightedOption> options = new ArrayList<>();
                
                if (finalOptionsSpec instanceof List<?> list) {
                    for (Object o : list) {
                        if (o instanceof Map<?, ?> m) {
                            // Check if this map is a weighted option wrapper
                            if (m.containsKey("weight") || m.containsKey("chance") || m.containsKey("prob")) {
                                Object val = m.get("value");
                                if (val == null) val = m.get("item");
                                if (val == null) val = m.get("option");
                                // If value is missing, maybe the map itself is the value? 
                                // But if it has "weight", it's likely a wrapper.
                                // If val is null, we might store null? Or maybe the user meant the map minus the weight?
                                // Let's assume if "value" key is missing, we use the map itself? No, that's ambiguous.
                                // Let's assume "value" is required for weighted wrapper.
                                
                                Double w = Resolvers.doubleVal(ctx, m, "weight", "chance", "prob", "probability");
                                double weight = w != null ? w : 1.0;
                                options.add(new WeightedOption(val, weight));
                            } else {
                                // Map as value
                                options.add(new WeightedOption(m, 1.0));
                            }
                        } else {
                            options.add(new WeightedOption(o, 1.0));
                        }
                    }
                } else if (finalOptionsSpec instanceof Map<?, ?> map) {
                    for (Map.Entry<?, ?> entry : map.entrySet()) {
                        Object val = entry.getKey();
                        Double w = Resolvers.doubleVal(ctx, entry.getValue());
                        double weight = w != null ? w : 1.0;
                        options.add(new WeightedOption(val, weight));
                    }
                }
                
                if (!options.isEmpty()) {
                    double totalWeight = 0;
                    for (WeightedOption opt : options) {
                        totalWeight += opt.weight;
                    }
                    
                    double r = ThreadLocalRandom.current().nextDouble() * totalWeight;
                    double count = 0;
                    for (WeightedOption opt : options) {
                        count += opt.weight;
                        if (r <= count) {
                            result = opt.value;
                            break;
                        }
                    }
                    if (result == null) result = options.get(options.size() - 1).value;
                }
            }
            
            Object finalVal = Resolvers.resolve(ctx, result);
            ctx.setGenericVarCompat(varName, finalVal);
            
            return ActionResult.ALLOW;
        };
    }
    
    private record WeightedOption(Object value, double weight) {}
}
