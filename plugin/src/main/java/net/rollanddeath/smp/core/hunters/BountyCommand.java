package net.rollanddeath.smp.core.hunters;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class BountyCommand implements CommandExecutor, TabCompleter {

    private final BountyManager bountyManager;

    public BountyCommand(BountyManager bountyManager) {
        this.bountyManager = bountyManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "poner":
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Solo jugadores pueden ofrecer recompensas."));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Uso: /cazadores poner <jugador> [cantidad] (usa el item en mano)"));
                    return true;
                }
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (target == null || (target.getName() == null && !target.hasPlayedBefore())) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Jugador objetivo no encontrado."));
                    return true;
                }
                if (target.getUniqueId().equals(player.getUniqueId())) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>No puedes poner recompensa sobre ti mismo."));
                    return true;
                }
                ItemStack hand = player.getInventory().getItemInMainHand();
                if (hand == null || hand.getType() == Material.AIR) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Debes tener en la mano el premio a ofrecer."));
                    return true;
                }
                int requestedAmount = hand.getAmount();
                if (args.length >= 3) {
                    try {
                        requestedAmount = Math.max(1, Integer.parseInt(args[2]));
                    } catch (NumberFormatException ignored) {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Cantidad inválida."));
                        return true;
                    }
                }
                if (requestedAmount > hand.getAmount()) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>No tienes tantas unidades en la mano."));
                    return true;
                }
                ItemStack deposit = hand.clone();
                deposit.setAmount(requestedAmount);
                int remaining = hand.getAmount() - requestedAmount;
                if (remaining <= 0) {
                    player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                } else {
                    hand.setAmount(remaining);
                }
                bountyManager.addBounty(player, target, deposit);
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Recompensa publicada por " + requestedAmount + "x " + deposit.getType().name().toLowerCase(Locale.ROOT) + " contra <yellow>" + safeName(target) + ""));
                return true;
            case "listar":
                showSummary(sender);
                return true;
            case "ver":
                if (args.length < 2) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Uso: /cazadores ver <jugador>"));
                    return true;
                }
                OfflinePlayer inspectTarget = Bukkit.getOfflinePlayer(args[1]);
                showTarget(sender, inspectTarget);
                return true;
            case "cancelar":
                if (!(sender instanceof Player cancelPlayer)) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Solo jugadores."));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<red>Uso: /cazadores cancelar <jugador>"));
                    return true;
                }
                OfflinePlayer cancelTarget = Bukkit.getOfflinePlayer(args[1]);
                int removed = bountyManager.cancelBounties(cancelPlayer, cancelTarget);
                if (removed == 0) {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>No tenías recompensas activas para ese jugador."));
                } else {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Has retirado " + removed + " aportes y se devolvieron a tu inventario."));
                }
                return true;
            default:
                sendUsage(sender);
                return true;
        }
    }

    private void showSummary(CommandSender sender) {
        Map<UUID, List<BountyManager.BountyReward>> data = bountyManager.snapshot();
        if (data.isEmpty()) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>No hay recompensas activas."));
            return;
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gold>Recompensas activas:"));
        data.forEach((targetId, rewards) -> {
            int stacks = rewards.stream().mapToInt(r -> r.item().getAmount()).sum();
            String anyItem = rewards.get(0).item().getType().name().toLowerCase(Locale.ROOT);
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>- <white>" + safeName(targetId) + " <gray>(" + stacks + " ítems, ej: " + anyItem + ")"));
        });
    }

    private void showTarget(CommandSender sender, OfflinePlayer inspectTarget) {
        List<BountyManager.BountyReward> rewards = bountyManager.getBountiesFor(inspectTarget.getUniqueId());
        if (rewards.isEmpty()) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Sin recompensas para " + safeName(inspectTarget) + "."));
            return;
        }
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gold>Recompensas por " + safeName(inspectTarget) + ":"));
        for (BountyManager.BountyReward reward : rewards) {
            String itemLine = reward.item().getAmount() + "x " + reward.item().getType().name().toLowerCase(Locale.ROOT);
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gray>- <white>" + reward.contributorName() + "<gray>: " + itemLine));
        }
    }

    private String safeName(OfflinePlayer player) {
        return player != null && player.getName() != null ? player.getName() : "(desconocido)";
    }

    private String safeName(UUID playerId) {
        OfflinePlayer offline = Bukkit.getOfflinePlayer(playerId);
        return safeName(offline);
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>/cazadores poner <jugador> [cantidad] - usa el item en mano"));
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>/cazadores listar - ver objetivos con recompensa"));
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>/cazadores ver <jugador> - detalles de los premios"));
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>/cazadores cancelar <jugador> - recupera tus aportes si no se cobraron"));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 0) {
            return Arrays.asList("poner", "listar", "ver", "cancelar");
        }

        if (args.length == 1) {
            return filter(args[0], Arrays.asList("poner", "listar", "ver", "cancelar"));
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        if (Arrays.asList("poner", "ver", "cancelar").contains(sub)) {
            List<String> players = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(Objects::nonNull)
                    .toList();
            return filter(args[1], players);
        }

        return Collections.emptyList();
    }

    private List<String> filter(String token, Collection<String> options) {
        String prefix = token.toLowerCase(Locale.ROOT);
        List<String> results = new ArrayList<>();
        for (String option : options) {
            if (option != null && option.toLowerCase(Locale.ROOT).startsWith(prefix)) {
                results.add(option);
            }
        }
        results.sort(String.CASE_INSENSITIVE_ORDER);
        return results;
    }
}
