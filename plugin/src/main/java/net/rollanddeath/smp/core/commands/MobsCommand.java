package net.rollanddeath.smp.core.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.MobType;
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
        Inventory inv = Bukkit.createInventory(null, 27, GUI_TITLE);
        List<MobType> activeMobs = plugin.getDailyMobRotationManager().getActiveMobs();

        if (activeMobs.isEmpty()) {
            ItemStack none = new ItemStack(Material.BARRIER);
            ItemMeta meta = none.getItemMeta();
            meta.displayName(Component.text("No hay mobs especiales activos", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            none.setItemMeta(meta);
            inv.setItem(13, none);
        } else {
            int slot = 0;
            for (MobType mob : activeMobs) {
                ItemStack item = new ItemStack(mob.isBoss() ? Material.WITHER_SKELETON_SKULL : Material.ZOMBIE_HEAD);
                ItemMeta meta = item.getItemMeta();
                
                NamedTextColor color = mob.isBoss() ? NamedTextColor.RED : NamedTextColor.YELLOW;
                meta.displayName(Component.text(mob.getDisplayName(), color).decoration(TextDecoration.ITALIC, false));
                
                List<Component> lore = new ArrayList<>();
                if (mob.isBoss()) {
                    lore.add(Component.text("☠ BOSS ☠", NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false));
                } else {
                    lore.add(Component.text("Mob Especial", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
                }
                lore.add(Component.empty());
                lore.add(Component.text("ID: " + mob.name(), NamedTextColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false));
                
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
