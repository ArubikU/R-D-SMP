package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.concurrent.ThreadLocalRandom;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.RoleType;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.entity.Player;

/** Assigns a random role to the player, optionally avoiding the current role. */
public final class AssignRandomRoleAction {
    private AssignRandomRoleAction() {
    }

    static void register() {
        ActionRegistrar.register("assign_random_role", AssignRandomRoleAction::parse, "random_role");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        boolean avoidCurrent = raw.get("avoid_current") instanceof Boolean b ? b : true;
        return ctx -> execute(ctx, avoidCurrent);
    }

    private static ActionResult execute(ScriptContext ctx, boolean avoidCurrent) {
        RollAndDeathSMP plugin = ctx.plugin();
        Player player = ctx.player();
        if (plugin == null || player == null) return ActionResult.ALLOW;

        var roleManager = plugin.getRoleManager();
        if (roleManager == null) return ActionResult.ALLOW;

        RoleType[] chosen = new RoleType[1];

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            try {
                RoleType current = roleManager.getPlayerRole(player);
                RoleType[] pool = RoleType.values();
                if (pool.length == 0) return;

                RoleType pick = pool[ThreadLocalRandom.current().nextInt(pool.length)];
                if (avoidCurrent && current != null && pool.length > 1) {
                    for (int i = 0; i < pool.length * 2; i++) {
                        RoleType candidate = pool[ThreadLocalRandom.current().nextInt(pool.length)];
                        if (candidate != current) {
                            pick = candidate;
                            break;
                        }
                    }
                }

                roleManager.setPlayerRole(player, pick);
                chosen[0] = pick;
            } catch (Exception ignored) {
            }
        });

        if (chosen[0] != null) {
            ctx.setGenericVarCompat("EVENT.custom.role", chosen[0].name());
            ctx.setGenericVarCompat("EVENT.custom.role_name", chosen[0].getName());
        }

        return ActionResult.ALLOW;
    }
}
