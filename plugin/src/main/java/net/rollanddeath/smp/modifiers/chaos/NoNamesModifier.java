package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import net.rollanddeath.smp.core.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public class NoNamesModifier extends Modifier {

    public NoNamesModifier(RollAndDeathSMP plugin) {
        super(plugin, "Sin Nombres", ModifierType.CHAOS, "Nametags de jugadores ocultos.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        org.bukkit.scoreboard.Team team = board.getTeam("hideNames");
        if (team == null) {
            team = board.registerNewTeam("hideNames");
        }
        team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            team.addEntry(player.getName());
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        org.bukkit.scoreboard.Team team = board.getTeam("hideNames");
        if (team != null) {
            team.unregister();
        }
    }
}
