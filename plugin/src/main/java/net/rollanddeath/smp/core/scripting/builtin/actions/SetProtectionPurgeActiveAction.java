package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;

/** Enables or disables protection purge on the protection manager. */
public final class SetProtectionPurgeActiveAction {
    private SetProtectionPurgeActiveAction() {
    }

    static void register() {
        ActionRegistrar.register("set_protection_purge_active", SetProtectionPurgeActiveAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Boolean value = Resolvers.bool(null, raw, "value", "active", "enabled");
        if (value == null) return null;
        boolean active = value;
        return ctx -> execute(ctx, active);
    }

    private static ActionResult execute(ScriptContext ctx, boolean active) {
        var plugin = ctx.plugin();
        if (plugin == null) return ActionResult.ALLOW;

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            try {
                if (plugin.getProtectionManager() != null) {
                    plugin.getProtectionManager().setPurgeActive(active);
                }
            } catch (Exception ignored) {
                // swallow to avoid script failures
            }
        });

        return ActionResult.ALLOW;
    }
}
