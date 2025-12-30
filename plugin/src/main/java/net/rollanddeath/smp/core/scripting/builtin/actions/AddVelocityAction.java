package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

final class AddVelocityAction {
    private AddVelocityAction() {
    }

    static void register() {
        ActionRegistrar.register("add_velocity", AddVelocityAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object xSpec = Resolvers.plain(raw, "x");
        Object ySpec = Resolvers.plain(raw, "y");
        Object zSpec = Resolvers.plain(raw, "z");
        if (xSpec == null && ySpec == null && zSpec == null) return null;
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            Double x = Resolvers.doubleVal(ctx, xSpec);
            Double y = Resolvers.doubleVal(ctx, ySpec);
            Double z = Resolvers.doubleVal(ctx, zSpec);

            double dx = x != null ? x : 0.0;
            double dy = y != null ? y : 0.0;
            double dz = z != null ? z : 0.0;

            Vector add = new Vector(dx, dy, dz);
            BuiltInActions.runSync(plugin, () -> player.setVelocity(player.getVelocity().add(add)));
            return ActionResult.ALLOW;
        };
    }
}