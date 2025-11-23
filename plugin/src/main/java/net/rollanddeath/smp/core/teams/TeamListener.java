package net.rollanddeath.smp.core.teams;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class TeamListener implements Listener {

    private final TeamManager teamManager;

    public TeamListener(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Player attacker = null;

        if (event.getDamager() instanceof Player p) {
            attacker = p;
        } else if (event.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player p) {
            attacker = p;
        }

        if (attacker == null || attacker.equals(victim)) return;

        Team attackerTeam = teamManager.getTeam(attacker.getUniqueId());
        Team victimTeam = teamManager.getTeam(victim.getUniqueId());

        if (attackerTeam != null && attackerTeam.equals(victimTeam)) {
            if (!attackerTeam.isFriendlyFire()) {
                event.setCancelled(true);
                attacker.sendMessage(Component.text("¡No puedes atacar a tu compañero de equipo!", NamedTextColor.RED));
            }
        }
    }
}
