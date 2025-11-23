package net.rollanddeath.smp.core.teams;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeamCommand implements CommandExecutor {

    private final TeamManager teamManager;

    public TeamCommand(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Solo jugadores pueden usar este comando.", NamedTextColor.RED));
            return true;
        }

        if (label.equalsIgnoreCase("tc")) {
            if (args.length == 0) {
                player.sendMessage(Component.text("Uso: /tc <mensaje>", NamedTextColor.RED));
                return true;
            }
            // Treat all args as chat message
            String message = String.join(" ", args);
            sendTeamMessage(player, message);
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "create":
                handleCreate(player, args);
                break;
            case "invite":
                handleInvite(player, args);
                break;
            case "accept":
                handleAccept(player);
                break;
            case "leave":
                handleLeave(player);
                break;
            case "kick":
                handleKick(player, args);
                break;
            case "chat":
            case "c":
                handleChat(player, args);
                break;
            case "friendlyfire":
            case "ff":
                handleFriendlyFire(player);
                break;
            case "war":
                handleWar(player, args);
                break;
            case "info":
                handleInfo(player);
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(Component.text("--- Comandos de Team ---", NamedTextColor.GOLD));
        player.sendMessage(Component.text("/team create <nombre> - Crear un equipo", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team invite <jugador> - Invitar a un jugador", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team accept - Aceptar invitación", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team leave - Salir del equipo", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team kick <jugador> - Expulsar miembro (Solo líder)", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team chat <msg> - Chat de equipo", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team ff - Alternar fuego amigo", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team war <equipo> - Declarar guerra", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team info - Ver información del equipo", NamedTextColor.YELLOW));
    }

    private void handleWar(Player player, String[] args) {
        Team attacker = teamManager.getTeam(player.getUniqueId());
        if (attacker == null) {
            player.sendMessage(Component.text("No estás en un equipo.", NamedTextColor.RED));
            return;
        }

        if (!attacker.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(Component.text("Solo el líder puede declarar la guerra.", NamedTextColor.RED));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(Component.text("Uso: /team war <equipo_objetivo>", NamedTextColor.RED));
            return;
        }

        String targetName = args[1];
        Team defender = teamManager.getTeam(targetName);
        
        if (defender == null) {
            player.sendMessage(Component.text("Equipo no encontrado.", NamedTextColor.RED));
            return;
        }
        
        if (attacker.equals(defender)) {
            player.sendMessage(Component.text("No puedes declarar la guerra a tu propio equipo.", NamedTextColor.RED));
            return;
        }

        if (attacker.isAtWarWith(defender.getName())) {
            player.sendMessage(Component.text("Ya estáis en guerra con este equipo.", NamedTextColor.RED));
            return;
        }

        teamManager.declareWar(attacker, defender);
    }

    private void handleChat(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Uso: /team chat <mensaje>", NamedTextColor.RED));
            return;
        }
        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        sendTeamMessage(player, message);
    }

    private void sendTeamMessage(Player player, String message) {
        Team team = teamManager.getTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(Component.text("No estás en un equipo.", NamedTextColor.RED));
            return;
        }

        Component chatFormat = Component.text("[TeamChat] ", NamedTextColor.AQUA)
                .append(Component.text(player.getName() + ": ", NamedTextColor.WHITE))
                .append(Component.text(message, NamedTextColor.GRAY));

        for (UUID memberId : team.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                member.sendMessage(chatFormat);
            }
        }
    }

    private void handleFriendlyFire(Player player) {
        Team team = teamManager.getTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(Component.text("No estás en un equipo.", NamedTextColor.RED));
            return;
        }

        if (!team.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(Component.text("Solo el líder puede cambiar esto.", NamedTextColor.RED));
            return;
        }

        boolean newState = !team.isFriendlyFire();
        team.setFriendlyFire(newState);
        
        Component msg = Component.text("Fuego Amigo ha sido ", NamedTextColor.YELLOW)
                .append(Component.text(newState ? "ACTIVADO" : "DESACTIVADO", newState ? NamedTextColor.RED : NamedTextColor.GREEN));
        
        for (UUID memberId : team.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                member.sendMessage(msg);
            }
        }
    }

    private void handleCreate(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Uso: /team create <nombre>", NamedTextColor.RED));
            return;
        }

        String name = args[1];
        if (teamManager.getTeam(player.getUniqueId()) != null) {
            player.sendMessage(Component.text("Ya estás en un equipo.", NamedTextColor.RED));
            return;
        }

        Team team = teamManager.createTeam(name, player.getUniqueId());
        if (team == null) {
            player.sendMessage(Component.text("El nombre del equipo ya existe o hubo un error.", NamedTextColor.RED));
        } else {
            player.sendMessage(Component.text("Equipo " + name + " creado con éxito!", NamedTextColor.GREEN));
        }
    }

    private void handleInvite(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Uso: /team invite <jugador>", NamedTextColor.RED));
            return;
        }

        Team team = teamManager.getTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(Component.text("No estás en un equipo.", NamedTextColor.RED));
            return;
        }

        if (!team.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(Component.text("Solo el líder puede invitar.", NamedTextColor.RED));
            return;
        }

        if (team.getMembers().size() >= 4) {
            player.sendMessage(Component.text("El equipo está lleno (Max 4).", NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(Component.text("Jugador no encontrado.", NamedTextColor.RED));
            return;
        }

        if (teamManager.getTeam(target.getUniqueId()) != null) {
            player.sendMessage(Component.text("El jugador ya está en un equipo.", NamedTextColor.RED));
            return;
        }

        teamManager.invitePlayer(target.getUniqueId(), team.getName());
        player.sendMessage(Component.text("Invitación enviada a " + target.getName(), NamedTextColor.GREEN));
        target.sendMessage(Component.text("Has sido invitado al equipo " + team.getName() + ". Usa /team accept para unirte.", NamedTextColor.GREEN));
    }

    private void handleAccept(Player player) {
        String teamName = teamManager.getInvite(player.getUniqueId());
        if (teamName == null) {
            player.sendMessage(Component.text("No tienes invitaciones pendientes.", NamedTextColor.RED));
            return;
        }

        if (teamManager.addMember(teamName, player.getUniqueId())) {
            player.sendMessage(Component.text("Te has unido al equipo " + teamName, NamedTextColor.GREEN));
            teamManager.clearInvite(player.getUniqueId());
            
            Team team = teamManager.getTeam(teamName);
            if (team != null) {
                for (UUID memberId : team.getMembers()) {
                    Player member = Bukkit.getPlayer(memberId);
                    if (member != null && !member.getUniqueId().equals(player.getUniqueId())) {
                        member.sendMessage(Component.text(player.getName() + " se ha unido al equipo.", NamedTextColor.GREEN));
                    }
                }
            }
        } else {
            player.sendMessage(Component.text("No se pudo unir al equipo (¿Lleno? ¿Ya no existe?).", NamedTextColor.RED));
        }
    }

    private void handleLeave(Player player) {
        Team team = teamManager.getTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(Component.text("No estás en un equipo.", NamedTextColor.RED));
            return;
        }

        teamManager.removeMember(player.getUniqueId());
        player.sendMessage(Component.text("Has salido del equipo.", NamedTextColor.GREEN));
    }

    private void handleKick(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Uso: /team kick <jugador>", NamedTextColor.RED));
            return;
        }

        Team team = teamManager.getTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(Component.text("No estás en un equipo.", NamedTextColor.RED));
            return;
        }

        if (!team.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(Component.text("Solo el líder puede expulsar.", NamedTextColor.RED));
            return;
        }

        String targetName = args[1];
        UUID targetUUID = null;
        for (UUID memberId : team.getMembers()) {
            String name = Bukkit.getOfflinePlayer(memberId).getName();
            if (name != null && name.equalsIgnoreCase(targetName)) {
                targetUUID = memberId;
                break;
            }
        }

        if (targetUUID == null) {
            player.sendMessage(Component.text("Jugador no encontrado en tu equipo.", NamedTextColor.RED));
            return;
        }

        if (targetUUID.equals(player.getUniqueId())) {
            player.sendMessage(Component.text("No puedes expulsarte a ti mismo. Usa /team leave.", NamedTextColor.RED));
            return;
        }

        teamManager.removeMember(targetUUID);
        player.sendMessage(Component.text("Jugador expulsado.", NamedTextColor.GREEN));
        
        Player target = Bukkit.getPlayer(targetUUID);
        if (target != null) {
            target.sendMessage(Component.text("Has sido expulsado del equipo.", NamedTextColor.RED));
        }
    }

    private void handleInfo(Player player) {
        Team team = teamManager.getTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(Component.text("No estás en un equipo.", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.text("--- Equipo: " + team.getName() + " ---", NamedTextColor.GOLD));
        String ownerName = Bukkit.getOfflinePlayer(team.getOwner()).getName();
        player.sendMessage(Component.text("Líder: " + (ownerName != null ? ownerName : "Desconocido"), NamedTextColor.YELLOW));
        player.sendMessage(Component.text("Miembros (" + team.getMembers().size() + "/4):", NamedTextColor.YELLOW));
        for (UUID memberId : team.getMembers()) {
            String memberName = Bukkit.getOfflinePlayer(memberId).getName();
            player.sendMessage(Component.text("- " + (memberName != null ? memberName : "Desconocido"), NamedTextColor.WHITE));
        }
    }
}
