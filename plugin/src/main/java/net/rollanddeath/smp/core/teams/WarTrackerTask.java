package net.rollanddeath.smp.core.teams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WarTrackerTask extends BukkitRunnable {

    private final TeamManager teamManager;

    public WarTrackerTask(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public void run() {
        for (Player p1 : Bukkit.getOnlinePlayers()) {
            Team t1 = teamManager.getTeam(p1.getUniqueId());
            if (t1 == null) continue;

            boolean enemyNearby = false;

            for (Player p2 : Bukkit.getOnlinePlayers()) {
                if (p1.equals(p2)) continue;
                
                Team t2 = teamManager.getTeam(p2.getUniqueId());
                if (t2 == null) continue;

                if (t1.isAtWarWith(t2.getName())) {
                    if (p1.getWorld().equals(p2.getWorld()) && p1.getLocation().distanceSquared(p2.getLocation()) < 10000) { // 100 blocks
                        enemyNearby = true;
                        break;
                    }
                }
            }

            if (enemyNearby) {
                // Apply glowing for 3 seconds (60 ticks)
                p1.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 60, 0, false, false));
            }
        }
    }
}
