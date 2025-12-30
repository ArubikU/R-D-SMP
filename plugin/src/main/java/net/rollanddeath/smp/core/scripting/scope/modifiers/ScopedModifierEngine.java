package net.rollanddeath.smp.core.scripting.scope.modifiers;

import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import net.rollanddeath.smp.core.scripting.scope.ScopeId;

import java.util.List;
import java.util.Map;

public final class ScopedModifierEngine {

    private ScopedModifierEngine() {
    }

    public static void applyAll(ScriptContext ctx, Map<ScopeId, List<ScopedModifier>> modifiersByScope) {
        if (ctx == null || modifiersByScope == null || modifiersByScope.isEmpty()) return;

        for (Map.Entry<ScopeId, List<ScopedModifier>> entry : modifiersByScope.entrySet()) {
            ScopeId scope = entry.getKey();
            if (ctx.scope(scope) == null) continue;

            List<ScopedModifier> mods = entry.getValue();
            if (mods == null || mods.isEmpty()) continue;

            for (ScopedModifier mod : mods) {
                boolean pass = ScriptEngine.allConditionsPass(ctx, List.of(mod.when()));
                if (!pass) continue;
                ActionResult r = ScriptEngine.runAllWithResult(ctx, mod.effects());
                // Si un efecto devuelve DENY, no es fatal aquí: los scoped_modifiers son un pipeline de variables.
                if (r != null && r.deny()) {
                    // no-op
                }
            }
        }
    }
}
