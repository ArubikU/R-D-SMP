package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;

final class SetMonstersSilentAllWorldsAction {
    private SetMonstersSilentAllWorldsAction() {}

    static void register() {
        ActionRegistrar.register("set_monsters_silent_all_worlds", SetMonstersSilentAllWorldsAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Boolean value = Resolvers.bool(null, raw, "value", "silent");
        boolean silent = value != null ? value : true;

        return ctx -> {
            ActionUtils.runSync(ctx.plugin(), () -> {
                for (World w : Bukkit.getWorlds()) {
                    for (Entity e : w.getEntities()) {
                        if (e instanceof Monster m) {
                            m.setSilent(silent);
                        }
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
