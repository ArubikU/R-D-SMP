package net.rollanddeath.smp.core.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.roles.RoleType;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuCommand implements CommandExecutor, Listener, TabCompleter {

    private final RollAndDeathSMP plugin;
    private final Component GUI_TITLE = Component.text("Menú Roll & Death", NamedTextColor.DARK_RED);

    public MenuCommand(RollAndDeathSMP plugin) {
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

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }

    private void openMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, GUI_TITLE);

        // 1. Active Events (Slot 11)
        ItemStack eventsItem = new ItemStack(Material.CLOCK);
        ItemMeta eventsMeta = eventsItem.getItemMeta();
        eventsMeta.displayName(Component.text("Eventos Activos", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
        List<Component> eventsLore = new ArrayList<>();
        if (plugin.getModifierManager().getActiveModifiers().isEmpty()) {
            eventsLore.add(Component.text("Ninguno", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        } else {
            for (String modName : plugin.getModifierManager().getActiveModifiers()) {
                Modifier mod = plugin.getModifierManager().getModifier(modName);
                if (mod != null) {
                    eventsLore.add(Component.text("• " + mod.getName(), NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                    eventsLore.add(Component.text("  " + mod.getDescription(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                }
            }
        }
        eventsMeta.lore(eventsLore);
        eventsItem.setItemMeta(eventsMeta);
        inv.setItem(11, eventsItem);

        // 2. Player Role (Slot 13)
        ItemStack roleItem = new ItemStack(Material.PLAYER_HEAD); 
        ItemMeta roleMeta = roleItem.getItemMeta();
        roleMeta.displayName(Component.text("Tu Rol", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false));
        List<Component> roleLore = new ArrayList<>();
        RoleType role = plugin.getRoleManager().getPlayerRole(player);
        if (role != null) {
            roleLore.add(Component.text(role.getName(), NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false));
            roleLore.add(Component.text(role.getDescription(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        } else {
            roleLore.add(Component.text("Sin Rol", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        }
        roleMeta.lore(roleLore);
        roleItem.setItemMeta(roleMeta);
        inv.setItem(13, roleItem);

        // 3. Stats / Lives (Slot 15)
        ItemStack statsItem = new ItemStack(Material.RED_DYE); 
        ItemMeta statsMeta = statsItem.getItemMeta();
        statsMeta.displayName(Component.text("Estadísticas", NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
        List<Component> statsLore = new ArrayList<>();
        int lives = plugin.getLifeManager().getLives(player);
        statsLore.add(Component.text("Vidas: " + lives, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        
        if (plugin.getTeamManager().getTeam(player.getUniqueId()) != null) {
             statsLore.add(Component.text("Equipo: " + plugin.getTeamManager().getTeam(player.getUniqueId()).getName(), NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        } else {
             statsLore.add(Component.text("Equipo: Ninguno", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
        }
        statsMeta.lore(statsLore);
        statsItem.setItemMeta(statsMeta);
        inv.setItem(15, statsItem);

        // Fill background
        ItemStack bg = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta bgMeta = bg.getItemMeta();
        bgMeta.displayName(Component.empty());
        bg.setItemMeta(bgMeta);
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, bg);
            }
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().title().equals(GUI_TITLE)) {
            event.setCancelled(true);
        }
    }
}
