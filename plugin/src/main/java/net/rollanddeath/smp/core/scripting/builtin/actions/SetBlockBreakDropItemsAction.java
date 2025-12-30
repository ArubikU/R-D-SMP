package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class SetBlockBreakDropItemsAction {
    private SetBlockBreakDropItemsAction() {
    }

    static void register() {
        ActionRegistrar.register("set_block_break_drop_items", SetBlockBreakDropItemsAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Boolean value = raw.get("value") instanceof Boolean b ? b : null;
        if (value == null) return null;
        return BuiltInActions.setBlockBreakDropItems(value);
    }
}
