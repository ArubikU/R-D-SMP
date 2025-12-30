package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.particles.ScriptedParticleSystemService;

final class StopParticleSystemAction {
    private StopParticleSystemAction() {}

    static void register() {
        ActionRegistrar.register("stop_particle_system", StopParticleSystemAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String id = Resolvers.string(null, raw, "id");
        String idKey = Resolvers.string(null, raw, "id_key");
        
        return ctx -> {
            var plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            ScriptedParticleSystemService svc = plugin.getScriptedParticleSystemService();
            if (svc == null) return ActionResult.ALLOW;

            String resolved = (id != null && !id.isBlank()) ? id : null;
            if (resolved == null && idKey != null && !idKey.isBlank()) {
                Object v = ctx.getValue(idKey);
                if (v != null) resolved = String.valueOf(v);
            }

            if (resolved != null && !resolved.isBlank()) {
                svc.stopSystem(resolved);
            }

            return ActionResult.ALLOW;
        };
    }
}
