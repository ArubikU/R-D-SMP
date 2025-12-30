package net.rollanddeath.smp.core.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EventMenuCommand implements CommandExecutor, TabCompleter, Listener {

    private final RollAndDeathSMP plugin;
    private final ModifierManager modifierManager;
    private final Component GUI_TITLE = Component.text("Eventos", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false);

    public EventMenuCommand(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.modifierManager = plugin.getModifierManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Solo jugadores.", NamedTextColor.RED));
            return true;
        }
        openMenu(player);
        return true;
    }

    @Override
    public @NotNull List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }

    private void openMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, GUI_TITLE);

        List<String> active = new ArrayList<>(modifierManager.getActiveModifiers());
        active.sort(Comparator.naturalOrder());

        int slot = 0;
        if (active.isEmpty()) {
            ItemStack none = new ItemStack(Material.BARRIER);
            ItemMeta meta = none.getItemMeta();
            meta.displayName(Component.text("No hay eventos activos", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            none.setItemMeta(meta);
            inv.setItem(slot, none);
        } else {
            for (String name : active) {
                Modifier mod = modifierManager.getModifier(name);
                if (mod == null) {
                    continue;
                }
                ItemStack item = new ItemStack(materialFor(mod.getType()));
                ItemMeta meta = item.getItemMeta();
                meta.displayName(Component.text(mod.getName(), toColor(mod.getType())).decoration(TextDecoration.ITALIC, false));
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text(typeLabel(mod.getType()), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                lore.add(Component.text(mod.getDescription(), NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
                meta.lore(lore);
                item.setItemMeta(meta);
                inv.setItem(slot++, item);
            }
        }

        // Recent history section
        List<String> history = new ArrayList<>(modifierManager.getEventHistory());
        Collections.reverse(history);
        slot = 36;
        int historyShown = 0;
        for (String name : history) {
            if (historyShown >= 18 || slot >= inv.getSize()) break;
            Modifier mod = modifierManager.getModifier(name);
            if (mod == null) continue;
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(mod.getName(), NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("(Reciente) " + typeLabel(mod.getType()), toColor(mod.getType())).decoration(TextDecoration.ITALIC, false));
            lore.add(Component.text(mod.getDescription(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
            historyShown++;
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().title().equals(GUI_TITLE)) {
            event.setCancelled(true);
        }
    }

    private Material materialFor(String type) {
        if (type == null) return Material.BOOK;
        return switch (type.toUpperCase()) {
            case "BLESSING" -> Material.EMERALD;
            case "CURSE" -> Material.WITHER_ROSE;
            case "CHAOS" -> Material.CLOCK;
            default -> Material.BOOK;
        };
    }

    private NamedTextColor toColor(String type) {
        if (type == null) return NamedTextColor.WHITE;
        return switch (type.toUpperCase()) {
            case "BLESSING" -> NamedTextColor.AQUA;
            case "CURSE" -> NamedTextColor.DARK_PURPLE;
            case "CHAOS" -> NamedTextColor.GOLD;
            default -> NamedTextColor.WHITE;
        };
    }

    private String typeLabel(String type) {
        if (type == null) return "Evento";
        return switch (type.toUpperCase()) {
            case "BLESSING" -> "Bendición";
            case "CURSE" -> "Maldición";
            case "CHAOS" -> "Caos";
            default -> type;
        };
    }
}
