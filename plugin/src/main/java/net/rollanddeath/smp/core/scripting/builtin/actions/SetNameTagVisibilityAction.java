package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Optional;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

final class SetNameTagVisibilityAction {
    private SetNameTagVisibilityAction() {
    }

    static void register() {
        ActionRegistrar.register("set_nametag_visibility", SetNameTagVisibilityAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String team = Optional.ofNullable(Resolvers.string(null, raw, "team")).orElse("hideNames");
        Boolean enabled = raw.get("enabled") instanceof Boolean b ? b : true;
        
        return ctx -> {
            ActionUtils.runSync(ctx.plugin(), () -> {
                Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
                Team t = sb.getTeam(team);
                if (t == null) {
                    t = sb.registerNewTeam(team);
                }
                if (enabled) {
                    t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
                } else {
                    t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
                }
                
                Player p = ctx.player();
                if (p != null) {
                    t.addEntry(p.getName());
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
