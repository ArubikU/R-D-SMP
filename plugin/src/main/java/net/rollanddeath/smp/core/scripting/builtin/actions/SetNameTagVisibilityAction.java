package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Optional;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;

final class SetNameTagVisibilityAction {
    private SetNameTagVisibilityAction() {
    }

    static void register() {
        ActionRegistrar.register("set_nametag_visibility", SetNameTagVisibilityAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String team = Optional.ofNullable(Resolvers.string(null, raw, "team")).orElse("hideNames");
        Boolean enabled = raw.get("enabled") instanceof Boolean b ? b : true;
        return BuiltInActions.setNameTagVisibility(team, enabled);
    }
}
