package net.rollanddeath.smp.core.roles;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RoleCommand implements CommandExecutor, TabCompleter {

    private final RollAndDeathSMP plugin;

    public RoleCommand(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "list":
                showAvailableRoles(sender);
                return true;
            case "choose":
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Solo jugadores pueden elegir rol."));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Uso: /role choose <rol>"));
                    return true;
                }
                RoleType chosen = parseRole(args[1], sender);
                if (chosen == null) return true;
                if (!plugin.getRoleManager().isRegistered(chosen)) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Ese rol no está disponible."));
                    return true;
                }
                plugin.getRoleManager().setPlayerRole(player, chosen);
                return true;
            case "clear":
                if (!(sender instanceof Player playerClear)) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Solo jugadores."));
                    return true;
                }
                plugin.getRoleManager().setPlayerRole(playerClear, null);
                return true;
            case "set":
                if (!sender.hasPermission("rd.admin.roles")) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>No tienes permiso."));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Uso: /role set <jugador> <rol>"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Jugador no encontrado."));
                    return true;
                }
                RoleType type = parseRole(args[2], sender);
                if (type == null) return true;
                plugin.getRoleManager().setPlayerRole(target, type);
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Rol establecido correctamente."));
                return true;
            default:
                sendUsage(sender);
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 0) {
            return Arrays.asList("list", "choose", "clear", "set");
        }

        if (args.length == 1) {
            return filterCompletions(args[0], Arrays.asList("list", "choose", "clear", "set"));
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        if (sub.equals("choose")) {
            List<String> roles = plugin.getRoleManager().getRegisteredRoles().stream()
                    .map(Enum::name)
                    .toList();
            return filterCompletions(args[1], roles);
        }

        if (sub.equals("set")) {
            if (args.length == 2) {
                List<String> players = Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(Objects::nonNull)
                        .toList();
                return filterCompletions(args[1], players);
            }
            if (args.length == 3) {
                List<String> roles = plugin.getRoleManager().getRegisteredRoles().stream()
                        .map(Enum::name)
                        .toList();
                return filterCompletions(args[2], roles);
            }
        }

        return Collections.emptyList();
    }

    private List<String> filterCompletions(String token, Collection<String> candidates) {
        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }

        String prefix = token.toLowerCase(Locale.ROOT);
        List<String> matches = new ArrayList<>();
        for (String option : candidates) {
            if (option != null && option.toLowerCase(Locale.ROOT).startsWith(prefix)) {
                matches.add(option);
            }
        }
        matches.sort(String.CASE_INSENSITIVE_ORDER);
        return matches;
    }

    private RoleType parseRole(String token, CommandSender sender) {
        try {
            return RoleType.valueOf(token.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Rol inválido."));
            showAvailableRoles(sender);
            return null;
        }
    }

    private void showAvailableRoles(CommandSender sender) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gold>Roles disponibles:"));
        plugin.getRoleManager().getRegisteredRoles().forEach(role ->
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>- <white>" + role.name() + " <dark_gray>| <gray>" + role.getDescription()))
        );
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Uso: /role list | /role choose <rol> | /role clear | /role set <jugador> <rol>"));
    }
}
