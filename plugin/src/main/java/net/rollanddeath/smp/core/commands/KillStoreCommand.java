package net.rollanddeath.smp.core.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import net.rollanddeath.smp.core.game.KillPointsManager;
import net.rollanddeath.smp.core.game.GameManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public class KillStoreCommand implements CommandExecutor, TabCompleter {

    private record StoreItem(String id, String label, int cost, int unlockDay, Supplier<List<ItemStack>> bundleSupplier, CustomItemType customItem) {}

    private final RollAndDeathSMP plugin;
    private final List<StoreItem> storeItems;

    public KillStoreCommand(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.storeItems = List.of(
                new StoreItem("bandage", "Venda Curativa", 1, 1, null, CustomItemType.HEALING_BANDAGE),
            new StoreItem("pearl", "Perlas de Ender x4", 2, 1, () -> List.of(createStack(Material.ENDER_PEARL, 4)), null),
            new StoreItem("rockets", "Cohetes x16", 3, 1, () -> List.of(createStack(Material.FIREWORK_ROCKET, 16)), null),
            new StoreItem("arrows", "Flechas x32", 2, 1, () -> List.of(createStack(Material.ARROW, 32)), null),
            new StoreItem("golden_apple", "Manzana Dorada x2", 2, 1, () -> List.of(createStack(Material.GOLDEN_APPLE, 2)), null),
            new StoreItem("gapple", "Manzana Encantada", 5, 1, () -> List.of(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE)), null),
            new StoreItem("totem", "Totem de la Inmortalidad", 8, 1, () -> List.of(new ItemStack(Material.TOTEM_OF_UNDYING)), null),
            new StoreItem("netherite", "Lingote de Netherita", 7, 1, () -> List.of(new ItemStack(Material.NETHERITE_INGOT)), null),
            new StoreItem("regen_totem", "Tótem de Regeneración", 9, 1, null, CustomItemType.REGENERATION_TOTEM),
            new StoreItem("grappling", "Gancho de Agarre", 5, 1, null, CustomItemType.GRAPPLING_HOOK),
            new StoreItem("armor_wings", "Alas Blindadas", 16, 5, null, CustomItemType.ARMORED_WINGS),
            new StoreItem("life_gapple", "Manzana de Vida", 12, 5, null, CustomItemType.LIFE_GAPPLE),
            new StoreItem("res_orb", "Orbe de Resurrección", 18, 7, null, CustomItemType.RESURRECTION_ORB),
            new StoreItem("notch_heart", "Corazón de Notch", 22, 10, null, CustomItemType.NOTCH_HEART),
                // Gear progresivo sin encantamientos
                        new StoreItem("set_acero", "Set de Acero (+armadura base)", 18, 10, () -> bundleFromTypes(
                    CustomItemType.STEEL_HELMET,
                    CustomItemType.STEEL_CHESTPLATE,
                    CustomItemType.STEEL_LEGGINGS,
                    CustomItemType.STEEL_BOOTS
                ), null),
                        new StoreItem("set_obsidiana", "Set Obsidiana (+armadura y dureza)", 30, 20, () -> bundleFromTypes(
                    CustomItemType.OBSIDIAN_HELMET,
                    CustomItemType.OBSIDIAN_CHESTPLATE,
                    CustomItemType.OBSIDIAN_LEGGINGS,
                    CustomItemType.OBSIDIAN_BOOTS
                ), null),
                        new StoreItem("set_vacio", "Set del Vacío (+armadura, dureza, KB)", 45, 30, () -> bundleFromTypes(
                    CustomItemType.VOID_HELMET,
                    CustomItemType.VOID_CHESTPLATE,
                    CustomItemType.VOID_LEGGINGS,
                    CustomItemType.VOID_BOOTS
                ), null),
                        new StoreItem("arma_acero", "Espada de Acero (+2.5 daño)", 10, 10, null, CustomItemType.STEEL_SWORD),
                        new StoreItem("arma_obsidiana", "Espada Obsidiana (+3.5 daño)", 15, 20, null, CustomItemType.OBSIDIAN_SWORD),
                        new StoreItem("arma_vacio", "Espada del Vacío (+4.5 daño)", 20, 30, null, CustomItemType.VOID_SWORD)
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Solo jugadores.", NamedTextColor.RED));
            return true;
        }

        KillPointsManager kpm = plugin.getKillPointsManager();
        if (kpm == null) {
            sender.sendMessage(Component.text("KillPointsManager no disponible.", NamedTextColor.RED));
            return true;
        }
        if (!kpm.isKillStoreEnabled()) {
            sender.sendMessage(Component.text("La Kill Store está desactivada.", NamedTextColor.RED));
            return true;
        }

        int balance = kpm.getPoints(player.getUniqueId());

        GameManager gm = plugin.getGameManager();
        int day = gm != null ? Math.max(1, gm.getCurrentDay()) : 1;

        if (args.length == 0) {
            sender.sendMessage(Component.text("=== Kill Store ===", NamedTextColor.GOLD));
            sender.sendMessage(Component.text("Tienes " + balance + " killpoints.", NamedTextColor.YELLOW));
            for (StoreItem item : storeItems) {
                if (day < item.unlockDay) {
                    sender.sendMessage(Component.text("(Día " + item.unlockDay + ") " + item.label + " - BLOQUEADO", NamedTextColor.DARK_GRAY));
                    continue;
                }
                sender.sendMessage(Component.text("/killstore buy " + item.id + " - " + item.label + " (" + item.cost + "kp)", NamedTextColor.GRAY));
            }
            return true;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("buy")) {
            String id = args[1].toLowerCase(Locale.ROOT);
            StoreItem item = storeItems.stream()
                    .filter(it -> it.id.equalsIgnoreCase(id))
                    .findFirst()
                    .orElse(null);
            if (item == null) {
                sender.sendMessage(Component.text("Ítem no encontrado. Usa /killstore para ver opciones.", NamedTextColor.RED));
                return true;
            }

            if (day < item.unlockDay) {
                sender.sendMessage(Component.text("Disponible desde el día " + item.unlockDay + ".", NamedTextColor.RED));
                return true;
            }

            if (balance < item.cost) {
                sender.sendMessage(Component.text("No tienes suficientes killpoints (" + balance + "/" + item.cost + ").", NamedTextColor.RED));
                return true;
            }

            int newBalance = balance - item.cost;
            kpm.setPoints(player.getUniqueId(), newBalance);

            if (item.customItem != null) {
                plugin.getItemManager().giveItem(player, item.customItem, 1);
            }
            if (item.bundleSupplier != null) {
                for (ItemStack stack : item.bundleSupplier.get()) {
                    player.getInventory().addItem(stack.clone());
                }
            }

            sender.sendMessage(Component.text("Compraste " + item.label + " por " + item.cost + "kp. Saldo: " + newBalance, NamedTextColor.GREEN));
            return true;
        }

        sender.sendMessage(Component.text("Uso: /killstore [buy <id>]", NamedTextColor.RED));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return filter(args[0], List.of("buy"));
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("buy")) {
            return filter(args[1], storeItems.stream().map(si -> si.id).toList());
        }
        return Collections.emptyList();
    }

    private List<String> filter(String token, Collection<String> options) {
        String lower = token.toLowerCase(Locale.ROOT);
        List<String> out = new ArrayList<>();
        for (String opt : options) {
            if (opt.toLowerCase(Locale.ROOT).startsWith(lower)) {
                out.add(opt);
            }
        }
        Collections.sort(out, String.CASE_INSENSITIVE_ORDER);
        return out;
    }

    private static ItemStack createStack(Material material, int amount) {
        return new ItemStack(material, amount);
    }

    private List<ItemStack> bundleFromTypes(CustomItemType... types) {
        List<ItemStack> items = new ArrayList<>();
        for (CustomItemType type : types) {
            items.add(customStack(type));
        }
        return items;
    }

    private ItemStack customStack(CustomItemType type) {
        CustomItem item = plugin.getItemManager().getItem(type);
        if (item == null) {
            return new ItemStack(Material.BARRIER);
        }
        return item.getItemStack().clone();
    }
}
