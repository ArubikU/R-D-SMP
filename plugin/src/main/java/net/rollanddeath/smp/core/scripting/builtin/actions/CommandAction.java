package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.integration.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

final class CommandAction {
    private CommandAction() {}

    static void register() {
        ActionRegistrar.register("command", CommandAction::parse, "cmd", "run_command");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String cmd = Resolvers.string(null, raw, "command", "cmd");
        String as = Resolvers.string(null, raw, "as", "executor"); // "console" or "player"
        
        if (cmd == null) return null;

        return ctx -> {
            Player p = ctx.player();
            String finalCmd = cmd;
            if (p != null) {
                finalCmd = PlaceholderUtil.resolvePlaceholders(ctx.plugin(), p, finalCmd);
                finalCmd = finalCmd.replace("%player%", p.getName());
            }
            
            final String commandToRun = finalCmd;
            boolean asConsole = "console".equalsIgnoreCase(as) || "server".equalsIgnoreCase(as);

            ActionUtils.runSync(ctx.plugin(), () -> {
                if (asConsole) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandToRun);
                } else if (p != null) {
                    p.performCommand(commandToRun);
                }
            });
            
            return ActionResult.ALLOW;
        };
    }
}
