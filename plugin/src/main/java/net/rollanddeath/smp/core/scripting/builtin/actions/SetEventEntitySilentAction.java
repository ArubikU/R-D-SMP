package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.entity.Entity;

final class SetEventEntitySilentAction {
    private SetEventEntitySilentAction() {}

    static void register() {
        ActionRegistrar.register("set_event_entity_silent", SetEventEntitySilentAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Boolean value = raw.get("value") instanceof Boolean b ? b : null;
        if (value == null) return null;

        return ctx -> {
            Entity entity = ctx.subjectOrEventEntity(Entity.class);
            if (entity == null) return ActionResult.ALLOW;
            
            ActionUtils.runSync(ctx.plugin(), () -> entity.setSilent(value));
            return ActionResult.ALLOW;
        };
    }
}
