package net.rollanddeath.smp.core.teams;

import net.rollanddeath.smp.core.LifeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class TeamBuffListener implements Listener {

    private static final double REDUCTION_PER_MEMBER = 0.05; // 5% por miembro online
    private static final int MIN_SIZE_FOR_BUFF = 4;
    private static final int MAX_COUNTED_MEMBERS = 5;

    private final TeamManager teamManager;
    private final LifeManager lifeManager;

    public TeamBuffListener(TeamManager teamManager, LifeManager lifeManager) {
        this.teamManager = teamManager;
        this.lifeManager = lifeManager;
    }

    @EventHandler
    public void onTeamDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        Team team = teamManager.getTeam(player.getUniqueId());
        if (team == null || team.getMembers().size() < MIN_SIZE_FOR_BUFF) return;

        int onlineAlive = countOnlineAlive(team);
        if (onlineAlive <= 1) return; // solo él

        double reduction = REDUCTION_PER_MEMBER * Math.min(MAX_COUNTED_MEMBERS, onlineAlive);
        double scaled = event.getDamage() * Math.max(0.0, 1.0 - reduction);
        event.setDamage(scaled);
    }

    @EventHandler
    public void onTeamExp(PlayerExpChangeEvent event) {
        Player earner = event.getPlayer();
        int amount = event.getAmount();
        if (amount <= 0) return;

        Team team = teamManager.getTeam(earner.getUniqueId());
        if (team == null || team.getMembers().size() < MIN_SIZE_FOR_BUFF) return;

        int share = (int) Math.floor(amount * 0.05); // 5% a cada compañero
        if (share <= 0) return;

        for (var memberId : team.getMembers()) {
            if (memberId.equals(earner.getUniqueId())) continue;
            Player teammate = Bukkit.getPlayer(memberId);
            if (teammate == null) continue;
            if (lifeManager != null && lifeManager.isEliminated(teammate)) continue;
            teammate.giveExp(share);
        }
    }

    private int countOnlineAlive(Team team) {
        int count = 0;
        for (var memberId : team.getMembers()) {
            Player p = Bukkit.getPlayer(memberId);
            if (p == null) continue;
            if (lifeManager != null && lifeManager.isEliminated(p)) continue;
            count++;
        }
        return count;
    }
}
