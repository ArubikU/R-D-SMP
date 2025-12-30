package net.rollanddeath.smp.core.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import net.rollanddeath.smp.core.mobs.scripted.ScriptedMob;
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

public class MobsCommand implements CommandExecutor, Listener {

    private final RollAndDeathSMP plugin;
    private final Component GUI_TITLE = Component.text("Mobs Activos", NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true);

    public MobsCommand(RollAndDeathSMP plugin) {
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
        Inventory inv = Bukkit.createInventory(null, 54, GUI_TITLE);
        List<String> activeMobs = plugin.getDailyMobRotationManager().getActiveMobs();

        // Tema del día (desde mobs.yml)
        try {
            String theme = plugin.getScriptedMobManager() != null
                ? plugin.getScriptedMobManager().getDailyThemeForDay(plugin.getGameManager().getCurrentDay())
                : null;
            if (theme != null && !theme.isBlank()) {
                ItemStack themeItem = new ItemStack(Material.PAPER);
                ItemMeta meta = themeItem.getItemMeta();
                meta.displayName(Component.text("Tema del día", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));
                Component themeComp;
                try {
                    themeComp = MiniMessage.miniMessage().deserialize(theme);
                } catch (Exception ignored) {
                    themeComp = Component.text(theme, NamedTextColor.YELLOW);
                }
                meta.lore(List.of(
                    Component.text("Día "+ plugin.getGameManager().getCurrentDay(), NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false),
                    Component.empty(),
                    themeComp.decoration(TextDecoration.ITALIC, false)
                ));
                themeItem.setItemMeta(meta);
                inv.setItem(4, themeItem);
            }
        } catch (Exception ignored) {
        }

        if (activeMobs.isEmpty()) {
            ItemStack none = new ItemStack(Material.BARRIER);
            ItemMeta meta = none.getItemMeta();
            meta.displayName(Component.text("No hay mobs especiales activos", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            none.setItemMeta(meta);
            inv.setItem(22, none);
        } else {
            int slot = 9; // deja la fila superior para info
            for (String mobId : activeMobs) {
                if (slot >= inv.getSize()) break;
                
                CustomMob mob = plugin.getMobManager().getMob(mobId);
                if (mob == null) continue;
                
                boolean isBoss = false;
                if (mob instanceof ScriptedMob sm) {
                    isBoss = sm.definition().isBoss();
                }

                ItemStack item = new ItemStack(isBoss ? Material.WITHER_SKELETON_SKULL : Material.ZOMBIE_HEAD);
                ItemMeta meta = item.getItemMeta();
                
                NamedTextColor color = isBoss ? NamedTextColor.RED : NamedTextColor.YELLOW;
                meta.displayName(Component.text(mob.getDisplayName(), color).decoration(TextDecoration.ITALIC, false));
                
                List<Component> lore = new ArrayList<>();
                if (isBoss) {
                    lore.add(Component.text("☠ BOSS ☠", NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false));
                } else {
                    lore.add(Component.text("Mob Especial", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                }
                lore.add(Component.empty());
                lore.add(Component.text("ID: " + mobId, NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
                
                meta.lore(lore);
                item.setItemMeta(meta);
                inv.setItem(slot++, item);
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
