package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

final class ForEachEntityAction {
    private ForEachEntityAction() {}

    static void register() {
        ActionRegistrar.register("for_each_entity_in_all_worlds", ForEachEntityAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        List<String> types = Resolvers.stringList(raw, "entity_types");
        String entityVar = Resolvers.string(null, raw, "entity_var", "var");
        List<Action> actions = Resolvers.parseActionList(raw.get("actions"));
        
        if (actions == null || actions.isEmpty()) return null;

        List<EntityType> entityTypes = new ArrayList<>();
        if (types != null) {
            for (String s : types) {
                try {
                    entityTypes.add(EntityType.valueOf(s.toUpperCase()));
                } catch (Exception ignored) {}
            }
        }

        return ctx -> {
            ActionUtils.runSync(ctx.plugin(), () -> {
                for (World w : Bukkit.getWorlds()) {
                    for (Entity e : w.getEntities()) {
                        if (entityTypes.isEmpty() || entityTypes.contains(e.getType())) {
                            ScriptContext child = new ScriptContext(ctx.plugin(), ctx.player(), ctx.subjectId(), ctx.phase(), ctx.variables());
                            if (entityVar != null) {
                                // If entityVar is SUBJECT, we need to override subject?
                                // ScriptContext subject is fixed.
                                // But we can set a variable.
                                // If the actions use SUBJECT, they might look at the context subject.
                                // If the user wants SUBJECT to be the entity, we might need a new context with that entity as subject.
                                // But ScriptContext expects Player or UUID subject?
                                // Let's check ScriptContext constructor.
                                // It takes UUID subjectId.
                                
                                if ("SUBJECT".equals(entityVar)) {
                                    // We can't easily change subject to a non-player entity if the context is player-based?
                                    // Actually subjectId is UUID.
                                    // So we can create a context with the entity's UUID.
                                    ScriptContext entityCtx = new ScriptContext(ctx.plugin(), ctx.player(), e.getUniqueId().toString(), ctx.phase(), ctx.variables());
                                    ScriptEngine.runAllWithResult(entityCtx, actions);
                                } else {
                                    child.setGenericVarCompat(entityVar, e);
                                    ScriptEngine.runAllWithResult(child, actions);
                                }
                            } else {
                                ScriptEngine.runAllWithResult(child, actions);
                            }
                        }
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
