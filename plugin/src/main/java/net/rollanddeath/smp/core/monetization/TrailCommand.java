package net.rollanddeath.smp.core.monetization;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TrailCommand implements CommandExecutor, TabCompleter {

    private static final List<Particle> ALLOWED = List.of(
            Particle.HEART,
            Particle.FLAME,
            Particle.TOTEM,
            Particle.VILLAGER_HAPPY,
            Particle.CRIT
    );

    private final MonetizationManager monetizationManager;

    public TrailCommand(MonetizationManager monetizationManager) {
        this.monetizationManager = monetizationManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Solo jugadores pueden usar este comando.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            monetizationManager.setTrail(player, null);
            player.sendMessage(Component.text("Trail desactivado.", NamedTextColor.GRAY));
            return true;
        }

        String name = args[0].toUpperCase(Locale.ROOT);
        Particle chosen = ALLOWED.stream().filter(p -> p.name().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (chosen == null) {
            player.sendMessage(Component.text("Trail inválido. Usa tab para ver opciones.", NamedTextColor.RED));
            return true;
        }

        if (!monetizationManager.canUseTrail(player, chosen)) {
            player.sendMessage(Component.text("No tienes permiso para este trail.", NamedTextColor.RED));
            return true;
        }

        monetizationManager.setTrail(player, chosen);
        player.sendMessage(Component.text("Trail activado: " + chosen.name(), NamedTextColor.GREEN));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return ALLOWED.stream()
                    .map(p -> p.name().toLowerCase(Locale.ROOT))
                    .filter(n -> n.startsWith(args[0].toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
