package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.entity.IronGolem;

final class SetIronGolemPlayerCreatedAction {
    private SetIronGolemPlayerCreatedAction() {}

    static void register() {
        ActionRegistrar.register("set_iron_golem_player_created", SetIronGolemPlayerCreatedAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Boolean created = raw.get("value") instanceof Boolean b ? b : null;
        if (created == null) return null;

        return ctx -> {
            IronGolem golem = ctx.subjectOrEventEntity(IronGolem.class);
            if (golem == null) return ActionResult.ALLOW;
            
            ActionUtils.runSync(ctx.plugin(), () -> golem.setPlayerCreated(created));
            return ActionResult.ALLOW;
        };
    }
}
