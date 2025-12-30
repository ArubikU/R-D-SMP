package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.entity.Vex;

final class SetVexChargingAction {
    private SetVexChargingAction() {
    }

    static void register() {
        ActionRegistrar.register("set_vex_charging", SetVexChargingAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        boolean charging = raw.get("value") instanceof Boolean b ? b : true;
        return ctx -> {
            Vex vex = ctx.subjectOrEventEntity(Vex.class);
            if (vex == null) return ActionResult.ALLOW;
            ActionUtils.runSync(ctx.plugin(), () -> vex.setCharging(charging));
            return ActionResult.ALLOW;
        };
    }
}
