package net.rollanddeath.smp.core.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.rules.DayRule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RulesCommand implements CommandExecutor, Listener {

    private final RollAndDeathSMP plugin;
    private final Component GUI_TITLE = Component.text("Reglas del Servidor", NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true);

    public RulesCommand(RollAndDeathSMP plugin) {
        this.plugin = plugin;
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

    private void openMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, GUI_TITLE);

        // Active Day Rules
        int currentDay = plugin.getGameManager().getCurrentDay();
        List<DayRule> activeRules = plugin.getDayRuleManager().getActiveRules(currentDay);

        if (!activeRules.isEmpty()) {
            int slot = 0;
            for (DayRule rule : activeRules) {
                ItemStack item = new ItemStack(Material.CLOCK);
                ItemMeta meta = item.getItemMeta();
                meta.displayName(Component.text("Regla del Día " + rule.getDay(), NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
                List<Component> lore = new ArrayList<>();
                lore.add(Component.text(rule.getName(), NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false));
                lore.add(Component.empty());
                lore.add(Component.text(rule.getDescription(), NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                meta.lore(lore);
                item.setItemMeta(meta);
                inv.setItem(slot++, item);
            }
        } else {
            ItemStack item = new ItemStack(Material.CLOCK);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text("Sin Reglas Diarias Activas", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            item.setItemMeta(meta);
            inv.setItem(4, item);
        }

        // General Rules
        addRule(inv, 19, "3 Vidas", "Empiezas con 3 vidas. Al llegar a 0, quedas fuera.", Material.TOTEM_OF_UNDYING);
        addRule(inv, 20, "Caos Acumulativo", "Cada día una nueva regla se suma a las anteriores.", Material.MAGMA_CREAM);
        addRule(inv, 21, "Roles", "Respeta tu rol semanal y sus limitaciones.", Material.PLAYER_HEAD);
        addRule(inv, 22, "Respeto", "No toxicidad extrema. Fair play ante todo.", Material.PAPER);
        addRule(inv, 23, "End Crystals", "Los cristales del End requieren 3 golpes para romperse.", Material.END_CRYSTAL);
        addRule(inv, 24, "Web", "Visita la web para ver todas las reglas detalladas.", Material.BOOK);
        addRule(inv, 25, "Eventos", "Asistencia obligatoria a la hora de la ruleta (00:00).", Material.BEACON);

        player.openInventory(inv);
    }

    private void addRule(Inventory inv, int slot, String title, String desc, Material mat) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(title, NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(desc, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().title().equals(GUI_TITLE)) {
            event.setCancelled(true);
        }
    }
}
