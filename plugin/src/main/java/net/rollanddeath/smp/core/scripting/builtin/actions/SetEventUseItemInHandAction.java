package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

final class SetEventUseItemInHandAction {
    private SetEventUseItemInHandAction() {}

    static void register() {
        ActionRegistrar.register("set_event_use_item_in_hand", SetEventUseItemInHandAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String value = Resolvers.string(null, raw, "value");
        if (value == null || value.isBlank()) return null;

        return ctx -> {
            Event ev = ctx.nativeEvent();
            if (ev instanceof PlayerInteractEvent pie) {
                try {
                    pie.setUseItemInHand(org.bukkit.event.Event.Result.valueOf(value.toUpperCase()));
                } catch (Exception ignored) {}
            }
            return ActionResult.ALLOW;
        };
    }
}
