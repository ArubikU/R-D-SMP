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
        if (!sender.hasPermission("rd.admin.roles")) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>No tienes permiso."));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Uso: /role set <jugador> <rol>"));
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Jugador no encontrado."));
                return true;
            }

            try {
                RoleType type = RoleType.valueOf(args[2].toUpperCase());
                plugin.getRoleManager().setPlayerRole(target, type);
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Rol establecido correctamente."));
            } catch (IllegalArgumentException e) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Rol inválido. Roles disponibles:"));
                for (RoleType type : RoleType.values()) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>- " + type.name()));
                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("rd.admin.roles")) {
            return Collections.emptyList();
        }

        if (args.length == 0) {
            return Collections.singletonList("set");
        }

        if (args.length == 1) {
            return filterCompletions(args[0], Collections.singletonList("set"));
        }

        if (!args[0].equalsIgnoreCase("set")) {
            return Collections.emptyList();
        }

        if (args.length == 2) {
            List<String> players = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(Objects::nonNull)
                    .toList();
            return filterCompletions(args[1], players);
        }

        if (args.length == 3) {
            List<String> roles = Arrays.stream(RoleType.values())
                    .map(Enum::name)
                    .toList();
            return filterCompletions(args[2], roles);
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
}
