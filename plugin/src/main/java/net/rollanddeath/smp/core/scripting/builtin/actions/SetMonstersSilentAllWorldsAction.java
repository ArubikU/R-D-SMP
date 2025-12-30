package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Monster;

final class SetMonstersSilentAllWorldsAction {
    private SetMonstersSilentAllWorldsAction() {}

    static void register() {
        ActionRegistrar.register("set_monsters_silent_all_worlds", SetMonstersSilentAllWorldsAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Boolean value = raw.get("value") instanceof Boolean b ? b : null;
        if (value == null) return null;

        return ctx -> {
            ActionUtils.runSync(ctx.plugin(), () -> {
                for (World w : Bukkit.getWorlds()) {
                    for (Monster m : w.getEntitiesByClass(Monster.class)) {
                        m.setSilent(value);
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
