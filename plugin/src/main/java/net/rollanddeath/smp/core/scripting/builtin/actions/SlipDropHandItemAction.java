package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class SlipDropHandItemAction {
    private SlipDropHandItemAction() {
    }

    static void register() {
        ActionRegistrar.register("slip_drop_hand_item", SlipDropHandItemAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String msg = Resolvers.string(null, raw, "message");
        String color = Resolvers.string(null, raw, "color");
        return BuiltInActions.slipDropHandItem(msg, color);
    }
}
