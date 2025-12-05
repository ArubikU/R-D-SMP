package net.rollanddeath.smp.core.items;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
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

public class ItemCommand implements CommandExecutor, TabCompleter {

    private final RollAndDeathSMP plugin;

    public ItemCommand(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("rollanddeath.admin")) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>No tienes permiso para usar este comando."));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Uso: /item give <jugador> <tipo> [cantidad]"));
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            Player target = plugin.getServer().getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Jugador no encontrado."));
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Especifica el tipo de item."));
                return true;
            }

            String itemTypeStr = args[2].toUpperCase();
            try {
                CustomItemType type = CustomItemType.valueOf(itemTypeStr);
                int amount = 1;
                if (args.length > 3) {
                    try {
                        amount = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Cantidad inválida."));
                        return true;
                    }
                }

                plugin.getItemManager().giveItem(target, type, amount);
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Item " + type.getDisplayName() + " entregado a " + target.getName()));
            } catch (IllegalArgumentException e) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Tipo de item inválido."));
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("rollanddeath.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 0) {
            return Collections.singletonList("give");
        }

        if (args.length == 1) {
            return filterCompletions(args[0], Collections.singletonList("give"));
        }

        if (!args[0].equalsIgnoreCase("give")) {
            return Collections.emptyList();
        }

        if (args.length == 2) {
            List<String> players = plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(Objects::nonNull)
                    .toList();
            return filterCompletions(args[1], players);
        }

        if (args.length == 3) {
            List<String> items = Arrays.stream(CustomItemType.values())
                    .map(Enum::name)
                    .toList();
            return filterCompletions(args[2], items);
        }

        if (args.length == 4) {
            return filterCompletions(args[3], Arrays.asList("1", "2", "4", "8", "16", "32", "64"));
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
