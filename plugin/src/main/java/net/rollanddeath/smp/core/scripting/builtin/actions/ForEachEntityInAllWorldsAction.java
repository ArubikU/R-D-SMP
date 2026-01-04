package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

final class ForEachEntityInAllWorldsAction {
    private ForEachEntityInAllWorldsAction() {
    }

    static void register() {
        ActionRegistrar.register("for_each_entity_in_all_worlds", ForEachEntityInAllWorldsAction::parse, "for_each_entity_all_worlds");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        List<Action> actions = Resolvers.parseActionList(raw.get("actions"));
        if (actions == null || actions.isEmpty()) return null;

        Object typesObj = raw.get("entity_types");
        List<String> entityTypes = null;
        if (typesObj instanceof List<?> typesList && !typesList.isEmpty()) {
            entityTypes = typesList.stream().filter(String.class::isInstance).map(String.class::cast).toList();
        }

        String varName = Optional.ofNullable(Resolvers.string(null, raw, "entity_var", "var", "as")).orElse("caster");
        
        List<String> finalEntityTypes = entityTypes;
        return ctx -> {
            for (World w : Bukkit.getWorlds()) {
                for (Entity e : w.getEntities()) {
                    if (finalEntityTypes != null && !finalEntityTypes.isEmpty()) {
                        if (!finalEntityTypes.contains(e.getType().name())) continue;
                    }

                    Map<String, Object> vars = new HashMap<>(ctx.variables());
                    vars.put("__subject", e);
                    
                    Player p = (e instanceof Player) ? (Player) e : ctx.player();
                    ScriptContext subCtx = new ScriptContext(ctx.plugin(), p, ctx.subjectId(), ctx.phase(), vars);
                    subCtx.setGenericVarCompat(varName, e);
                    
                    ScriptEngine.runAllWithResult(subCtx, actions);
                }
            }
            return ActionResult.ALLOW;
        };
    }
}
