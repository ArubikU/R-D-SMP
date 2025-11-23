package net.rollanddeath.smp.core.mobs;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MobCommand implements CommandExecutor {

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
}
