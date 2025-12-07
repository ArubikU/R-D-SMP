package net.rollanddeath.smp.core.teams;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.combat.CombatLogManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class TeamCommand implements CommandExecutor, TabCompleter {

        private final RollAndDeathSMP plugin;
        private final TeamManager teamManager;
        private final CombatLogManager combatLogManager;
        private final Map<UUID, BukkitTask> homeWarmups = new HashMap<>();
        private final Map<UUID, Long> homeCooldowns = new HashMap<>();
    private static final List<String> BASE_SUBCOMMANDS = Arrays.asList(
            "create",
            "invite",
            "accept",
            "leave",
            "kick",
            "chat",
            "togglechat",
            "friendlyfire",
            "war",
            "info",
            "color",
            "home",
            "sethome"
    );

        private static final List<String> COLOR_OPTIONS = Arrays.asList(
            "red",
            "dark_red",
            "gold",
            "yellow",
            "green",
            "dark_green",
            "aqua",
            "dark_aqua",
            "blue",
            "dark_blue",
            "light_purple",
            "dark_purple",
            "white",
            "gray",
            "dark_gray",
            "black"
        );

    public TeamCommand(RollAndDeathSMP plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.combatLogManager = plugin.getCombatLogManager();
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
            teamManager.sendTeamMessage(player, message);
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
            case "togglechat":
                handleToggleChat(player);
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
            case "color":
                handleColor(player, args);
                break;
            case "home":
                handleHome(player);
                break;
            case "sethome":
                handleSetHome(player);
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        }

        if (alias.equalsIgnoreCase("tc")) {
            return Collections.emptyList();
        }

        if (args.length == 0) {
            return new ArrayList<>(BASE_SUBCOMMANDS);
        }

        if (args.length == 1) {
            return filterCompletions(args[0], BASE_SUBCOMMANDS);
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "invite":
                if (args.length == 2) {
                    Team team = teamManager.getTeam(player.getUniqueId());
                    if (team == null || !team.getOwner().equals(player.getUniqueId()) || team.getMembers().size() >= 4) {
                        return Collections.emptyList();
                    }

                    List<String> candidates = Bukkit.getOnlinePlayers().stream()
                            .filter(p -> !p.getUniqueId().equals(player.getUniqueId()))
                            .filter(p -> teamManager.getTeam(p.getUniqueId()) == null)
                            .map(Player::getName)
                            .filter(Objects::nonNull)
                            .toList();
                    return filterCompletions(args[1], candidates);
                }
                break;
            case "kick":
                if (args.length == 2) {
                    Team team = teamManager.getTeam(player.getUniqueId());
                    if (team == null || !team.getOwner().equals(player.getUniqueId())) {
                        return Collections.emptyList();
                    }

                    List<String> members = team.getMembers().stream()
                            .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                            .filter(Objects::nonNull)
                            .filter(name -> !name.equalsIgnoreCase(player.getName()))
                            .toList();
                    return filterCompletions(args[1], members);
                }
                break;
            case "war":
                if (args.length == 2) {
                    Team team = teamManager.getTeam(player.getUniqueId());
                    if (team == null || !team.getOwner().equals(player.getUniqueId())) {
                        return Collections.emptyList();
                    }

                    List<String> targets = teamManager.getTeamNames().stream()
                            .filter(name -> !name.equalsIgnoreCase(team.getName()))
                            .filter(name -> !team.isAtWarWith(name))
                            .toList();
                    return filterCompletions(args[1], targets);
                }
                break;
            case "color":
                if (args.length == 2) {
                    return filterCompletions(args[1], COLOR_OPTIONS);
                }
                break;
            case "chat":
            case "c":
                return Collections.emptyList();
            default:
                break;
        }

        return Collections.emptyList();
    }

    private void sendHelp(Player player) {
        player.sendMessage(Component.text("--- Comandos de Team ---", NamedTextColor.GOLD));
        player.sendMessage(Component.text("/team create <nombre> - Crear un equipo", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team invite <jugador> - Invitar a un jugador", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team accept - Aceptar invitación", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team leave - Salir del equipo", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team kick <jugador> - Expulsar miembro (Solo líder)", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team chat <msg> - Chat de equipo", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team togglechat - Alterna que todo tu chat vaya al team", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team ff - Alternar fuego amigo", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team war <equipo> - Declarar guerra", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team info - Ver información del equipo", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team color <color> - Cambiar el color del equipo", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team sethome - Establecer home del equipo (líder)", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("/team home - Volver al home del equipo", NamedTextColor.YELLOW));
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

    private void handleColor(Player player, String[] args) {
        Team team = teamManager.getTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(Component.text("No estás en un equipo.", NamedTextColor.RED));
            return;
        }

        if (!team.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(Component.text("Solo el líder puede cambiar el color del equipo.", NamedTextColor.RED));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(Component.text("Uso: /team color <color>", NamedTextColor.RED));
            return;
        }

        String requested = args[1].toLowerCase(Locale.ROOT);
        NamedTextColor color = NamedTextColor.NAMES.value(requested);
        if (color == null || !COLOR_OPTIONS.contains(requested)) {
            player.sendMessage(Component.text("Color inválido. Opciones: " + String.join(", ", COLOR_OPTIONS), NamedTextColor.RED));
            return;
        }

        team.setColor(color);

        Component notice = Component.text("Color del equipo actualizado a ", NamedTextColor.YELLOW)
                .append(Component.text(requested, color));
        for (UUID memberId : team.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                member.sendMessage(notice);
            }
        }
    }

    private void handleChat(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(Component.text("Uso: /team chat <mensaje>", NamedTextColor.RED));
            return;
        }
        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        teamManager.sendTeamMessage(player, message);
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
        // The following block is duplicated and has been removed.
        // String ownerName = Bukkit.getOfflinePlayer(team.getOwner()).getName();
        // player.sendMessage(Component.text("Líder: " + (ownerName != null ? ownerName : "Desconocido"), NamedTextColor.YELLOW));
        // player.sendMessage(Component.text("Miembros (" + team.getMembers().size() + "/4):", NamedTextColor.YELLOW));
        // for (UUID memberId : team.getMembers()) {
        //     String memberName = Bukkit.getOfflinePlayer(memberId).getName();
        //     player.sendMessage(Component.text("- " + (memberName != null ? memberName : "Desconocido"), NamedTextColor.WHITE));
        // }
        }

    private void handleKick(Player player, String[] args) {
        Team team = teamManager.getTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(Component.text("No estás en un equipo.", NamedTextColor.RED));
            return;
        }

        if (!team.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(Component.text("Solo el líder puede expulsar miembros.", NamedTextColor.RED));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(Component.text("Uso: /team kick <jugador>", NamedTextColor.RED));
            return;
        }

        String targetName = args[1];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        UUID targetId = target.getUniqueId();

        if (!team.getMembers().contains(targetId)) {
            player.sendMessage(Component.text("Ese jugador no es miembro de tu equipo.", NamedTextColor.RED));
            return;
        }

        if (targetId.equals(team.getOwner())) {
            player.sendMessage(Component.text("No puedes expulsar al líder.", NamedTextColor.RED));
            return;
        }

        teamManager.removeMember(targetId);
        player.sendMessage(Component.text("Has expulsado a " + targetName + " del equipo.", NamedTextColor.GREEN));

        Player onlineTarget = Bukkit.getPlayer(targetId);
        if (onlineTarget != null) {
            onlineTarget.sendMessage(Component.text("Has sido expulsado del equipo " + team.getName() + ".", NamedTextColor.RED));
        }

        for (UUID memberId : team.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null && !memberId.equals(player.getUniqueId())) {
                member.sendMessage(Component.text(targetName + " ha sido expulsado del equipo.", NamedTextColor.YELLOW));
            }
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

    private void handleSetHome(Player player) {
        Team team = teamManager.getTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(Component.text("No estás en un equipo.", NamedTextColor.RED));
            return;
        }

        if (!team.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(Component.text("Solo el líder puede establecer el home.", NamedTextColor.RED));
            return;
        }

        Location loc = player.getLocation();
        team.setHome(loc);
        player.sendMessage(Component.text("Home del equipo establecido en tu posición.", NamedTextColor.GREEN));
    }

    private void handleHome(Player player) {
        Team team = teamManager.getTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(Component.text("No estás en un equipo.", NamedTextColor.RED));
            return;
        }

        Location home = team.getHome();
        if (home == null) {
            player.sendMessage(Component.text("Tu equipo no tiene un home establecido.", NamedTextColor.RED));
            return;
        }

        if (combatLogManager != null && combatLogManager.isEnabled()) {
            int remaining = combatLogManager.getRemainingCombatSeconds(player.getUniqueId());
            if (remaining > 0) {
                player.sendMessage(Component.text("No puedes usar /team home en combate. Espera " + remaining + "s.", NamedTextColor.RED));
                return;
            }
        }

        long now = System.currentTimeMillis();
        long lastUse = homeCooldowns.getOrDefault(player.getUniqueId(), 0L);
        if (now - lastUse < 20000) {
            long waitMs = 20000 - (now - lastUse);
            long waitSec = (long) Math.ceil(waitMs / 1000.0);
            player.sendMessage(Component.text("Cooldown: espera " + waitSec + "s para usar /team home otra vez.", NamedTextColor.RED));
            return;
        }

        // Cancel any existing warmup
        BukkitTask existing = homeWarmups.remove(player.getUniqueId());
        if (existing != null) {
            existing.cancel();
        }

        Location start = player.getLocation().clone();
        player.sendMessage(Component.text("Mantente quieto 5s para teletransportarte al home...", NamedTextColor.YELLOW));

        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            homeWarmups.remove(player.getUniqueId());

            if (!player.isOnline()) return;

            if (combatLogManager != null && combatLogManager.isEnabled()) {
                int remaining = combatLogManager.getRemainingCombatSeconds(player.getUniqueId());
                if (remaining > 0) {
                    player.sendMessage(Component.text("Cancelado: entraste en combate.", NamedTextColor.RED));
                    return;
                }
            }

            Location current = player.getLocation();
            if (!sameBlockPos(start, current)) {
                player.sendMessage(Component.text("Te moviste. Vuelve a intentarlo y quédate quieto 5s.", NamedTextColor.RED));
                return;
            }

            if (home.getWorld() == null) {
                player.sendMessage(Component.text("El home del equipo es inválido (mundo no encontrado).", NamedTextColor.RED));
                return;
            }

            player.teleport(home);
            player.sendMessage(Component.text("Teletransportado al home del equipo.", NamedTextColor.GREEN));
            homeCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        }, 100L); // 5s warmup

        homeWarmups.put(player.getUniqueId(), task);
    }

    private boolean sameBlockPos(Location a, Location b) {
        if (a.getWorld() == null || b.getWorld() == null) return false;
        if (!a.getWorld().equals(b.getWorld())) return false;
        return a.getBlockX() == b.getBlockX() && a.getBlockY() == b.getBlockY() && a.getBlockZ() == b.getBlockZ();
    }

    private void handleToggleChat(Player player) {
        Team team = teamManager.getTeam(player.getUniqueId());
        if (team == null) {
            player.sendMessage(Component.text("No estás en un equipo.", NamedTextColor.RED));
            return;
        }

        boolean enabled = teamManager.toggleTeamChat(player.getUniqueId());
        if (enabled) {
            player.sendMessage(Component.text("Chat de equipo automático ACTIVADO. Tus mensajes van al team.", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("Chat de equipo automático DESACTIVADO.", NamedTextColor.YELLOW));
        }
    }

    private List<String> filterCompletions(String token, Collection<String> candidates) {
        if (token.isEmpty()) {
            List<String> all = new ArrayList<>(candidates);
            all.sort(String.CASE_INSENSITIVE_ORDER);
            return all;
        }

        List<String> matches = new ArrayList<>();
        String lower = token.toLowerCase(Locale.ROOT);
        for (String option : candidates) {
            if (option != null && option.toLowerCase(Locale.ROOT).startsWith(lower)) {
                matches.add(option);
            }
        }
        matches.sort(String.CASE_INSENSITIVE_ORDER);
        return matches;
    }
}
