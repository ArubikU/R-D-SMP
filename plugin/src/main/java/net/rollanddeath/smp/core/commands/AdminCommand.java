package net.rollanddeath.smp.core.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class AdminCommand implements CommandExecutor {

    private final RollAndDeathSMP plugin;

    public AdminCommand(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("admin")) {
             if (!sender.hasPermission("rd.admin")) {
                 sender.sendMessage(Component.text("No tienes permiso.", NamedTextColor.RED));
                 return true;
             }
             handleAdmin(sender, Arrays.copyOfRange(args, 1, args.length));
             return true;
        }
        
        sender.sendMessage(Component.text("RollAndDeath SMP Plugin v1.0", NamedTextColor.GOLD));
        return true;
    }

    private void handleAdmin(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "roulette":
                if (args.length > 1 && args[1].equalsIgnoreCase("spin")) {
                    plugin.getModifierManager().startRandomModifier();
                    sender.sendMessage(Component.text("¡Ruleta girada!", NamedTextColor.GREEN));
                    Bukkit.broadcast(Component.text("¡La Ruleta ha girado! Un nuevo evento ha comenzado...", NamedTextColor.GOLD));
                } else {
                    sender.sendMessage(Component.text("Uso: /rd admin roulette spin", NamedTextColor.RED));
                }
                break;
            case "life":
                if (args.length >= 3 && args[1].equalsIgnoreCase("set")) {
                    Player target = Bukkit.getPlayer(args[2]);
                    if (target == null) {
                        sender.sendMessage(Component.text("Jugador no encontrado.", NamedTextColor.RED));
                        return;
                    }
                    try {
                        int amount = Integer.parseInt(args[3]);
                        plugin.getLifeManager().setLives(target, amount);
                        sender.sendMessage(Component.text("Vidas de " + target.getName() + " establecidas a " + amount, NamedTextColor.GREEN));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Component.text("Número inválido.", NamedTextColor.RED));
                    }
                } else {
                    sender.sendMessage(Component.text("Uso: /rd admin life set <player> <amount>", NamedTextColor.RED));
                }
                break;
            case "role":
                if (args.length >= 3 && args[1].equalsIgnoreCase("set")) {
                    Player target = Bukkit.getPlayer(args[2]);
                    if (target == null) {
                        sender.sendMessage(Component.text("Jugador no encontrado.", NamedTextColor.RED));
                        return;
                    }
                    String roleName = args[3].toUpperCase();
                    try {
                        RoleType type = RoleType.valueOf(roleName);
                        plugin.getRoleManager().setPlayerRole(target, type);
                        sender.sendMessage(Component.text("Rol de " + target.getName() + " establecido a " + type.getName(), NamedTextColor.GREEN));
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(Component.text("Rol inválido. Roles disponibles: " + Arrays.toString(RoleType.values()), NamedTextColor.RED));
                    }
                } else {
                    sender.sendMessage(Component.text("Uso: /rd admin role set <player> <role>", NamedTextColor.RED));
                }
                break;
            case "event":
                if (args.length >= 3 && args[1].equalsIgnoreCase("add")) {
                    String eventName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                    Modifier mod = plugin.getModifierManager().getModifier(eventName);
                    if (mod != null) {
                        plugin.getModifierManager().activateModifier(eventName);
                        sender.sendMessage(Component.text("Evento activado: " + eventName, NamedTextColor.GREEN));
                    } else {
                        sender.sendMessage(Component.text("Evento no encontrado.", NamedTextColor.RED));
                    }
                } else {
                    sender.sendMessage(Component.text("Uso: /rd admin event add <event_name>", NamedTextColor.RED));
                }
                break;
            default:
                sendHelp(sender);
                break;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("--- Comandos de Admin RollAndDeath ---", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/rd admin roulette spin - Girar ruleta", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/rd admin life set <player> <amount> - Setear vidas", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/rd admin role set <player> <role> - Setear rol", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/rd admin event add <name> - Activar evento", NamedTextColor.YELLOW));
    }
}
