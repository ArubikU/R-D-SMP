package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.entity.Zombie;

final class SetZombieBabyAction {
    private SetZombieBabyAction() {}

    static void register() {
        ActionRegistrar.register("set_zombie_baby", SetZombieBabyAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Boolean baby = raw.get("value") instanceof Boolean b ? b : null;
        if (baby == null) return null;

        return ctx -> {
            Zombie zombie = ctx.subjectOrEventEntity(Zombie.class);
            if (zombie == null) return ActionResult.ALLOW;
            
            ActionUtils.runSync(ctx.plugin(), () -> zombie.setBaby(baby));
            return ActionResult.ALLOW;
        };
    }
}
