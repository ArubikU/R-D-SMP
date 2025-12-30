package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.event.entity.EntityDeathEvent;

final class ClearEventDeathDropsAction {
    private ClearEventDeathDropsAction() {}

    static void register() {
        ActionRegistrar.register("clear_event_death_drops", ClearEventDeathDropsAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        return ctx -> {
            EntityDeathEvent ede = ctx.nativeEvent(EntityDeathEvent.class);
            if (ede != null) {
                ede.getDrops().clear();
            }
            return ActionResult.ALLOW;
        };
    }
}
