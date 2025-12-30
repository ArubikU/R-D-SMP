package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class SetBeeAngerAction {
    private SetBeeAngerAction() {
    }

    static void register() {
        ActionRegistrar.register("set_bee_anger", SetBeeAngerAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Integer ticks = Resolvers.integer(null, raw, "ticks");
        if (ticks == null || ticks < 0) return null;
        return BuiltInActions.setBeeAnger(ticks);
    }
}
