package net.rollanddeath.smp.core.items;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ItemCommand implements CommandExecutor {

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
}
