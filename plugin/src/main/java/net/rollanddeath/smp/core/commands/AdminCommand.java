package net.rollanddeath.smp.core.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItemType;
import net.rollanddeath.smp.core.mobs.MobType;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.roles.RoleType;
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

public class AdminCommand implements CommandExecutor, TabCompleter {

    private final RollAndDeathSMP plugin;
    private static final List<String> ADMIN_SUBCOMMANDS = Arrays.asList("roulette", "setday", "life", "role", "event", "item", "mob", "discord", "down", "revive", "announce", "toggle");
    private static final List<String> EVENT_SUBCOMMANDS = Arrays.asList("add", "remove", "clear", "list");

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

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 0) {
            return Collections.singletonList("admin");
        }

        if (args.length == 1) {
            return filterCompletions(args[0], Collections.singletonList("admin"));
        }

        if (!args[0].equalsIgnoreCase("admin")) {
            return Collections.emptyList();
        }

        if (!sender.hasPermission("rd.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 2) {
            return filterCompletions(args[1], ADMIN_SUBCOMMANDS);
        }

        String sub = args[1].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "roulette":
                if (args.length == 3) {
                    return filterCompletions(args[2], Collections.singletonList("spin"));
                }
                break;
            case "setday":
                return Collections.emptyList();
            case "life":
                if (args.length == 3) {
                    return filterCompletions(args[2], Collections.singletonList("set"));
                } else if (args.length >= 4) {
                    if (!args[2].equalsIgnoreCase("set")) {
                        return Collections.emptyList();
                    }
                    if (args.length == 4) {
                        List<String> players = Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .filter(Objects::nonNull)
                                .toList();
                        return filterCompletions(args[3], players);
                    }
                    if (args.length == 5) {
                        return filterCompletions(args[4], Arrays.asList("1", "2", "3", "4", "5", "10"));
                    }
                }
                break;
            case "role":
                if (args.length == 3) {
                    return filterCompletions(args[2], Collections.singletonList("set"));
                } else if (args.length >= 4) {
                    if (!args[2].equalsIgnoreCase("set")) {
                        return Collections.emptyList();
                    }
                    if (args.length == 4) {
                        List<String> players = Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .filter(Objects::nonNull)
                                .toList();
                        return filterCompletions(args[3], players);
                    }
                    if (args.length == 5) {
                        List<String> roles = Arrays.stream(RoleType.values())
                                .map(Enum::name)
                                .toList();
                        return filterCompletions(args[4], roles);
                    }
                }
                break;
            case "down":
            case "revive":
                if (args.length == 3) {
                    List<String> players = Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(Objects::nonNull)
                            .toList();
                    return filterCompletions(args[2], players);
                }
                break;
            case "event":
                if (args.length == 3) {
                    return filterCompletions(args[2], EVENT_SUBCOMMANDS);
                }
                if (args.length >= 4) {
                    String action = args[2].toLowerCase(Locale.ROOT);
                    if (action.equals("add")) {
                        List<String> modifiers = new ArrayList<>(plugin.getModifierManager().getRegisteredModifierNames());
                        return filterCompletions(args[3], modifiers);
                    }
                    if (action.equals("remove")) {
                        List<String> active = new ArrayList<>(plugin.getModifierManager().getActiveModifiers());
                        return filterCompletions(args[3], active);
                    }
                }
                break;
            case "item":
                if (args.length == 3) {
                    return filterCompletions(args[2], Collections.singletonList("give"));
                }
                if (!args[2].equalsIgnoreCase("give")) {
                    return Collections.emptyList();
                }
                if (args.length == 4) {
                    List<String> players = Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(Objects::nonNull)
                            .toList();
                    return filterCompletions(args[3], players);
                }
                if (args.length == 5) {
                    List<String> items = Arrays.stream(CustomItemType.values())
                            .map(Enum::name)
                            .toList();
                    return filterCompletions(args[4], items);
                }
                if (args.length == 6) {
                    return filterCompletions(args[5], Arrays.asList("1", "2", "4", "8", "16", "32", "64"));
                }
                break;
            case "toggle":
                if (args.length == 3) {
                    return filterCompletions(args[2], Arrays.asList("killpoints", "killstore", "pvp"));
                }
                if (args.length == 4) {
                    return filterCompletions(args[3], Arrays.asList("on", "off"));
                }
                break;
            case "mob":
                if (args.length == 3) {
                    return filterCompletions(args[2], Collections.singletonList("spawn"));
                }
                if (!args[2].equalsIgnoreCase("spawn")) {
                    return Collections.emptyList();
                }
                if (args.length == 4) {
                    List<String> mobs = Arrays.stream(MobType.values())
                            .map(Enum::name)
                            .toList();
                    return filterCompletions(args[3], mobs);
                }
                if (args.length == 5) {
                    List<String> players = Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(Objects::nonNull)
                            .toList();
                    return filterCompletions(args[4], players);
                }
                if (args.length == 6) {
                    return filterCompletions(args[5], Arrays.asList("1", "2", "3", "5", "10"));
                }
                break;
            case "discord":
                if (args.length == 3) {
                    return Collections.singletonList("<mensaje>");
                }
                return Collections.emptyList();
            case "announce":
                if (args.length >= 3) {
                    return Collections.emptyList();
                }
                return Collections.singletonList("<mensaje>");
            default:
                return Collections.emptyList();
        }

        return Collections.emptyList();
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
                    plugin.getModifierManager().spinRoulette();
                    sender.sendMessage(Component.text("¡Ruleta girada!", NamedTextColor.GREEN));
                } else {
                    sender.sendMessage(Component.text("Uso: /rd admin roulette spin", NamedTextColor.RED));
                }
                break;
            case "setday":
                if (args.length >= 2) {
                    try {
                        int day = Integer.parseInt(args[1]);
                        plugin.getGameManager().setDay(day);
                        sender.sendMessage(Component.text("Día establecido a " + day, NamedTextColor.GREEN));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Component.text("Número inválido.", NamedTextColor.RED));
                    }
                } else {
                    sender.sendMessage(Component.text("Uso: /rd admin setDay <day>", NamedTextColor.RED));
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
                } else if (args.length >= 3 && args[1].equalsIgnoreCase("remove")) {
                    String eventName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                    if (plugin.getModifierManager().isActive(eventName)) {
                        plugin.getModifierManager().deactivateModifier(eventName);
                        sender.sendMessage(Component.text("Evento desactivado: " + eventName, NamedTextColor.GREEN));
                    } else {
                        sender.sendMessage(Component.text("El evento no está activo o no existe.", NamedTextColor.RED));
                    }
                } else if (args.length >= 2 && args[1].equalsIgnoreCase("clear")) {
                    plugin.getModifierManager().clearAllModifiers();
                    sender.sendMessage(Component.text("Todos los eventos han sido desactivados.", NamedTextColor.GREEN));
                } else if (args.length >= 2 && args[1].equalsIgnoreCase("list")) {
                    sender.sendMessage(Component.text("Eventos disponibles:", NamedTextColor.GOLD));
                    for (String name : plugin.getModifierManager().getRegisteredModifierNames()) {
                        sender.sendMessage(Component.text("- " + name, NamedTextColor.YELLOW));
                    }
                } else {
                    sender.sendMessage(Component.text("Uso: /rd admin event <add|remove|clear|list> [name]", NamedTextColor.RED));
                }
                break;
            case "item":
                handleItemCommand(sender, args);
                break;
            case "mob":
                handleMobCommand(sender, args);
                break;
            case "discord":
                handleDiscordCommand(sender, args);
                break;
            case "announce":
                handleAnnounceCommand(sender, args);
                break;
            case "toggle":
                handleToggleCommand(sender, args);
                break;
            case "down":
                handleDownCommand(sender, args);
                break;
            case "revive":
                handleReviveCommand(sender, args);
                break;
            default:
                sendHelp(sender);
                break;
        }
    }

    private void handleAnnounceCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Uso: /rd admin announce <mensaje>", NamedTextColor.RED));
            return;
        }
        String raw = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        Component msg = MiniMessage.miniMessage().deserialize(raw);
        Bukkit.broadcast(msg);
        sender.sendMessage(Component.text("Anuncio enviado.", NamedTextColor.GREEN));
    }

    private void handleDownCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Uso: /rd admin down <player>", NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(Component.text("Jugador no encontrado.", NamedTextColor.RED));
            return;
        }

        boolean success = plugin.getReanimationManager().adminForceDown(target);
        if (success) {
            sender.sendMessage(Component.text("Has tumbado a " + target.getName() + ".", NamedTextColor.GOLD));
        } else {
            sender.sendMessage(Component.text("No se pudo tumbar (ya está down o inválido).", NamedTextColor.RED));
        }
    }

    private void handleReviveCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("Uso: /rd admin revive <player>", NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(Component.text("Jugador no encontrado.", NamedTextColor.RED));
            return;
        }

        boolean success = plugin.getReanimationManager().adminForceRevive(target);
        if (success) {
            sender.sendMessage(Component.text("Has levantado a " + target.getName() + ".", NamedTextColor.GOLD));
        } else {
            sender.sendMessage(Component.text("No se pudo levantar (no está down o inválido).", NamedTextColor.RED));
        }
    }

    private void handleItemCommand(CommandSender sender, String[] args) {
        if (args.length < 2 || !args[1].equalsIgnoreCase("give")) {
            sender.sendMessage(Component.text("Uso: /rd admin item give <player> <item> [amount]", NamedTextColor.RED));
            return;
        }

        if (args.length < 4) {
            sender.sendMessage(Component.text("Uso: /rd admin item give <player> <item> [amount]", NamedTextColor.RED));
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(Component.text("Jugador no encontrado.", NamedTextColor.RED));
            return;
        }

        String itemName = args[3].toUpperCase(Locale.ROOT);
        CustomItemType type;
        try {
            type = CustomItemType.valueOf(itemName);
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(Component.text("Item inválido. Usa tab para ver opciones.", NamedTextColor.RED));
            return;
        }

        int amount = 1;
        if (args.length >= 5) {
            try {
                amount = Integer.parseInt(args[4]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(Component.text("Cantidad inválida.", NamedTextColor.RED));
                return;
            }
            if (amount <= 0) {
                sender.sendMessage(Component.text("La cantidad debe ser mayor a 0.", NamedTextColor.RED));
                return;
            }
        }

        plugin.getItemManager().giveItem(target, type, amount);
        sender.sendMessage(Component.text("Entregado " + amount + "x " + type.getDisplayName() + " a " + target.getName(), NamedTextColor.GREEN));
        target.sendMessage(Component.text("Has recibido " + amount + "x " + type.getDisplayName() + ".", NamedTextColor.GOLD));
    }

    private void handleMobCommand(CommandSender sender, String[] args) {
        if (args.length < 2 || !args[1].equalsIgnoreCase("spawn")) {
            sender.sendMessage(Component.text("Uso: /rd admin mob spawn <mob> [player] [cantidad]", NamedTextColor.RED));
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(Component.text("Uso: /rd admin mob spawn <mob> [player] [cantidad]", NamedTextColor.RED));
            return;
        }

        String mobName = args[2].toUpperCase(Locale.ROOT);
        MobType type;
        try {
            type = MobType.valueOf(mobName);
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(Component.text("Mob inválido. Usa tab para ver opciones.", NamedTextColor.RED));
            return;
        }

        Player target = null;
        if (args.length >= 4) {
            target = Bukkit.getPlayer(args[3]);
            if (target == null) {
                sender.sendMessage(Component.text("Jugador no encontrado.", NamedTextColor.RED));
                return;
            }
        } else if (sender instanceof Player playerSender) {
            target = playerSender;
        }

        if (target == null) {
            sender.sendMessage(Component.text("Debes indicar un jugador cuando usas la consola.", NamedTextColor.RED));
            return;
        }

        int amount = 1;
        if (args.length >= 5) {
            try {
                amount = Integer.parseInt(args[4]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(Component.text("Cantidad inválida.", NamedTextColor.RED));
                return;
            }
            if (amount <= 0) {
                sender.sendMessage(Component.text("La cantidad debe ser mayor a 0.", NamedTextColor.RED));
                return;
            }
        }

        int spawned = 0;
        for (int i = 0; i < amount; i++) {
            if (plugin.getMobManager().spawnMob(type, target.getLocation()) != null) {
                spawned++;
            }
        }

        if (spawned == 0) {
            sender.sendMessage(Component.text("No se pudo invocar el mob solicitado.", NamedTextColor.RED));
            return;
        }

        sender.sendMessage(Component.text("Invocado " + spawned + "x " + type.getDisplayName() + " en " + target.getName(), NamedTextColor.GREEN));
        if (!target.equals(sender)) {
            target.sendMessage(Component.text("Un administrador invocó " + spawned + "x " + type.getDisplayName() + " cerca de ti.", NamedTextColor.YELLOW));
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("--- Comandos de Admin RollAndDeath ---", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/rd admin roulette spin - Girar ruleta", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/rd admin setDay <day> - Establecer día y girar ruleta", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/rd admin life set <player> <amount> - Setear vidas", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/rd admin role set <player> <role> - Setear rol", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/rd admin event add <name> - Activar evento", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/rd admin event remove <name> - Desactivar evento", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/rd admin event clear - Desactivar todos los eventos", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/rd admin event list - Listar eventos disponibles", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/rd admin item give <player> <item> [amount] - Dar ítem personalizado", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/rd admin mob spawn <mob> [player] [cantidad] - Invocar mob personalizado", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/rd admin discord <mensaje> - Enviar anuncio a Discord", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/rd admin announce <mensaje> - Anuncio global (MiniMessage)", NamedTextColor.YELLOW));
    }
    
    private void handleDiscordCommand(CommandSender sender, String[] args) {
        if (plugin.getDiscordService() == null || !plugin.getDiscordService().isEnabled()) {
            sender.sendMessage(Component.text("La integración de Discord no está configurada.", NamedTextColor.RED));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("Uso: /rd admin discord <mensaje>", NamedTextColor.RED));
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        plugin.getDiscordService().sendAdminAnnouncement(sender, message);

        Component announcement = Component.text("[Anuncio] ", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(message, NamedTextColor.WHITE));
        Bukkit.broadcast(announcement);
        sender.sendMessage(Component.text("Anuncio enviado a Discord.", NamedTextColor.GREEN));
    }

    private void handleToggleCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Component.text("Uso: /rd admin toggle <killpoints|killstore|pvp> <on|off>", NamedTextColor.RED));
            return;
        }

        if (plugin.getKillPointsManager() == null) {
            sender.sendMessage(Component.text("KillPointsManager no disponible.", NamedTextColor.RED));
            return;
        }

        String target = args[2].toLowerCase(Locale.ROOT);
        boolean enable;
        if (args.length >= 3) {
            String flag = args[3].toLowerCase(Locale.ROOT);
            if (flag.equals("on")) {
                enable = true;
            } else if (flag.equals("off")) {
                enable = false;
            } else {
                sender.sendMessage(Component.text("Debes usar on/off.", NamedTextColor.RED));
                return;
            }
        } else {
            sender.sendMessage(Component.text("Debes indicar on/off.", NamedTextColor.RED));
            return;
        }

        switch (target) {
            case "killpoints" -> {
                plugin.getKillPointsManager().setKillPointsEnabled(enable);
                sender.sendMessage(Component.text("Killpoints " + (enable ? "activados" : "desactivados"), NamedTextColor.GREEN));
            }
            case "killstore" -> {
                plugin.getKillPointsManager().setKillStoreEnabled(enable);
                sender.sendMessage(Component.text("Kill Store " + (enable ? "activada" : "desactivada"), NamedTextColor.GREEN));
            }
            case "pvp" -> {
                plugin.getKillPointsManager().setPvpEnabled(enable);
                sender.sendMessage(Component.text("PvP " + (enable ? "activado" : "desactivado"), NamedTextColor.GREEN));
            }
            default -> sender.sendMessage(Component.text("Opción desconocida. Usa killpoints/killstore/pvp.", NamedTextColor.RED));
        }
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
