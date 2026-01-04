package net.rollanddeath.smp.core.monetization;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class MonetizationCommand implements CommandExecutor, TabCompleter {

    private final MonetizationManager monetizationManager;

    public MonetizationCommand(MonetizationManager monetizationManager) {
        this.monetizationManager = monetizationManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Solo jugadores pueden usar este comando", NamedTextColor.RED));
            return true;
        }

        String name = command.getName().toLowerCase();
        switch (name) {
            case "ec":
            case "ender":
            case "enderchest":
                if (!monetizationManager.canUseVirtualEnder(player)) {
                    player.sendMessage(Component.text("No tienes permiso para abrir el ender chest aquí.", NamedTextColor.RED));
                    return true;
                }
                monetizationManager.openEnderChest(player);
                return true;
            case "craft":
                if (!monetizationManager.canUseCraft(player)) {
                    player.sendMessage(Component.text("No tienes permiso para /craft.", NamedTextColor.RED));
                    return true;
                }
                monetizationManager.openCraft(player);
                return true;
            case "anvil":
                if (!monetizationManager.canUseAnvil(player)) {
                    player.sendMessage(Component.text("No tienes permiso para /anvil.", NamedTextColor.RED));
                    return true;
                }
                monetizationManager.openAnvil(player);
                return true;
            case "enchant":
            case "enchanting":
                if (!monetizationManager.canUseEnchant(player)) {
                    player.sendMessage(Component.text("No tienes permiso para /enchant.", NamedTextColor.RED));
                    return true;
                }
                monetizationManager.openEnchant(player);
                return true;
            case "stonecutter":
                if (!monetizationManager.canUseStonecutter(player)) {
                    player.sendMessage(Component.text("No tienes permiso para /stonecutter.", NamedTextColor.RED));
                    return true;
                }
                monetizationManager.openStonecutter(player);
                return true;
            case "smith":
            case "smithing":
                if (!monetizationManager.canUseSmith(player)) {
                    player.sendMessage(Component.text("No tienes permiso para /smith.", NamedTextColor.RED));
                    return true;
                }
                monetizationManager.openSmith(player);
                return true;
            case "loom":
                if (!monetizationManager.canUseLoom(player)) {
                    player.sendMessage(Component.text("No tienes permiso para /loom.", NamedTextColor.RED));
                    return true;
                }
                monetizationManager.openLoom(player);
                return true;
            case "grindstone":
                if (!monetizationManager.canUseGrindstone(player)) {
                    player.sendMessage(Component.text("No tienes permiso para /grindstone.", NamedTextColor.RED));
                    return true;
                }
                monetizationManager.openGrindstone(player);
                return true;
            case "cartography":
                if (!monetizationManager.canUseCartography(player)) {
                    player.sendMessage(Component.text("No tienes permiso para /cartography.", NamedTextColor.RED));
                    return true;
                }
                monetizationManager.openCartography(player);
                return true;
            case "backpack":
                if (!monetizationManager.canUseBackpack(player)) {
                    player.sendMessage(Component.text("No tienes permiso para /backpack.", NamedTextColor.RED));
                    return true;
                }
                monetizationManager.openBackpack(player);
                return true;
            case "trash":
                if (!monetizationManager.canUseTrash(player)) {
                    player.sendMessage(Component.text("No tienes permiso para /trash.", NamedTextColor.RED));
                    return true;
                }
                monetizationManager.openTrash(player);
                return true;
            case "furnace":
                if (!monetizationManager.canUseFurnace(player)) {
                    player.sendMessage(Component.text("No tienes permiso para /furnace.", NamedTextColor.RED));
                    return true;
                }
                monetizationManager.openFurnace(player);
                return true;
            case "blast":
            case "blastfurnace":
                if (!monetizationManager.canUseBlast(player)) {
                    player.sendMessage(Component.text("No tienes permiso para /blast.", NamedTextColor.RED));
                    return true;
                }
                monetizationManager.openBlast(player);
                return true;
            case "smoker":
                if (!monetizationManager.canUseSmoker(player)) {
                    player.sendMessage(Component.text("No tienes permiso para /smoker.", NamedTextColor.RED));
                    return true;
                }
                monetizationManager.openSmoker(player);
                return true;
            default:
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
