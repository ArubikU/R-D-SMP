package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.builtin.conditions.ConditionRegistrar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.builtin.conditions.ConditionRegistrar;

final class SelectEntitiesAction {
    private SelectEntitiesAction() {}

    static void register() {
        ActionRegistrar.register("select_entities", SelectEntitiesAction::parse, "select_entity", "find_entities");
    }

    private static Action parse(Map<?, ?> raw) {
        String storeKey = Resolvers.string(null, raw, "store", "var", "key");
        if (storeKey == null || storeKey.isBlank()) return null;

        String source = Resolvers.string(null, raw, "source"); // world, nearby, server
        Object locationSpec = raw.get("location"); // for nearby
        Double radius = Resolvers.doubleVal(null, raw, "radius", "r");
        
        Object typeSpec = raw.get("type"); // EntityType or list of types
        String tag = Resolvers.string(null, raw, "tag");
        String name = Resolvers.string(null, raw, "name");
        
        Integer limit = Resolvers.integer(null, raw, "limit", "count");
        String sort = Resolvers.string(null, raw, "sort"); // nearest, furthest, random
        
        List<Condition> conditions = new ArrayList<>();
        if (raw.get("conditions") instanceof List<?> l) {
            for (Object o : l) {
                if (o instanceof Map<?, ?> m) {
                    Condition c = ConditionRegistrar.parse(m);
                    if (c != null) conditions.add(c);
                }
            }
        }

        return ctx -> {
            List<Entity> filtered = Resolvers.selectEntities(ctx, raw, conditions);

            // Store
            if (limit != null && limit == 1) {
                ctx.setGenericVarCompat(storeKey, filtered.isEmpty() ? null : filtered.get(0));
            } else {
                ctx.setGenericVarCompat(storeKey, filtered);
            }

            return ActionResult.ALLOW;
        };
    }
}
