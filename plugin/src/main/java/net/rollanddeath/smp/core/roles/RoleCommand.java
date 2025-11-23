package net.rollanddeath.smp.core.roles;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RoleCommand implements CommandExecutor {

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

        if (args.length < 2) {
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
}
