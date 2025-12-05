package net.rollanddeath.smp.core.mobs;

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

public class MobCommand implements CommandExecutor, TabCompleter {

    private final RollAndDeathSMP plugin;

    public MobCommand(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Solo los jugadores pueden usar este comando."));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("rollanddeath.admin")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No tienes permiso para usar este comando."));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Uso: /mob spawn <tipo>"));
            return true;
        }

        if (args[0].equalsIgnoreCase("spawn")) {
            String mobName = args[1].toUpperCase();
            try {
                MobType type = MobType.valueOf(mobName);
                plugin.getMobManager().spawnMob(type, player.getLocation());
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Mob " + type.getDisplayName() + " spawneado."));
            } catch (IllegalArgumentException e) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Tipo de mob inválido."));
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        }

        if (!player.hasPermission("rollanddeath.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 0) {
            return Collections.singletonList("spawn");
        }

        if (args.length == 1) {
            return filterCompletions(args[0], Collections.singletonList("spawn"));
        }

        if (!args[0].equalsIgnoreCase("spawn")) {
            return Collections.emptyList();
        }

        if (args.length == 2) {
            List<String> mobs = Arrays.stream(MobType.values())
                    .map(Enum::name)
                    .toList();
            return filterCompletions(args[1], mobs);
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
