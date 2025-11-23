package net.rollanddeath.smp.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.core.roles.Role;
import net.rollanddeath.smp.core.roles.RoleManager;
import net.rollanddeath.smp.core.roles.RoleType;
import net.rollanddeath.smp.core.teams.Team;
import net.rollanddeath.smp.core.teams.TeamManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    private final TeamManager teamManager;
    private final RoleManager roleManager;

    public ChatListener(TeamManager teamManager, RoleManager roleManager) {
        this.teamManager = teamManager;
        this.roleManager = roleManager;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.renderer((source, sourceDisplayName, message, viewer) -> {
            Component formatted = Component.empty();

            // Team Tag
            Team team = teamManager.getTeam(source.getUniqueId());
            if (team != null) {
                formatted = formatted.append(Component.text("[" + team.getName() + "] ", NamedTextColor.AQUA));
            }

            // Role Tag
            RoleType role = roleManager.getPlayerRole(source);
            if (role != null) {
                formatted = formatted.append(Component.text("[" + role.getName() + "] ", NamedTextColor.GOLD));
            }

            // Name
            formatted = formatted.append(sourceDisplayName.color(NamedTextColor.WHITE));
            
            // Separator
            formatted = formatted.append(Component.text(": ", NamedTextColor.GRAY));

            // Message
            formatted = formatted.append(message.color(NamedTextColor.WHITE));

            return formatted;
        });
    }
}
