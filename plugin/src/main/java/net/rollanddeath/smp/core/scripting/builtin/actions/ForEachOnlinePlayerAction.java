package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class ForEachOnlinePlayerAction {
    private ForEachOnlinePlayerAction() {
    }

    static void register() {
        ActionRegistrar.register("for_each_online_player", ForEachOnlinePlayerAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        List<Action> actions = Resolvers.parseActionList(raw.get("actions"));
        if (actions == null || actions.isEmpty()) return null;
        return BuiltInActions.forEachOnlinePlayer(actions);
    }
}
