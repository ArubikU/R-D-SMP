package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.event.entity.EntityPickupItemEvent;

final class RemoveEventItemEntityAction {
    private RemoveEventItemEntityAction() {}

    static void register() {
        ActionRegistrar.register("remove_event_item_entity", RemoveEventItemEntityAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        return ctx -> {
            if (ctx.nativeEvent() instanceof EntityPickupItemEvent epi) {
                epi.getItem().remove();
            }
            return ActionResult.ALLOW;
        };
    }
}
