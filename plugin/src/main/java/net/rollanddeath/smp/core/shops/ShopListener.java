package net.rollanddeath.smp.core.shops;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ShopListener implements Listener {

    private final RollAndDeathSMP plugin;
    private final ShopManager manager;

    private final Map<UUID, CreationSession> creationSessions = new ConcurrentHashMap<>();
    private final Map<UUID, PurchaseSession> purchaseSessions = new ConcurrentHashMap<>();
    private final Map<UUID, OwnerSession> ownerSessions = new ConcurrentHashMap<>();

    public ShopListener(RollAndDeathSMP plugin, ShopManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(SignChangeEvent event) {
        String line = event.getLine(0);
        if (line == null || !line.trim().equalsIgnoreCase("[tienda]")) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();
        Block containerBlock = getAttachedContainer(block);
        if (containerBlock == null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Coloca el cartel pegado a un cofre/barril/shulker."));
            return;
        }

        Location signLoc = block.getLocation();
        Location containerLoc = containerBlock.getLocation();
        if (manager.get(signLoc) != null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Ya existe una tienda en este cartel."));
            return;
        }

        creationSessions.put(player.getUniqueId(), new CreationSession(signLoc, containerLoc));
        event.setLine(0, "[TIENDA]");
        event.setLine(1, "Configurando...");
        event.setLine(2, "Sigue el chat");
        event.setLine(3, "");
        player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Escribe <green>ok</green> con el item a vender en tu mano principal. Escribe <red>cancelar</red> para abortar." )
            .append(Component.space())
            .append(buttons(
                "<click:suggest_command:'ok'><green>[OK]</green></click>",
                "<click:suggest_command:'cancelar'><red>[Cancelar]</red></click>"
            )));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();
        Shop shop = manager.get(loc);
        if (shop != null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<red>No puedes romper un cartel de tienda."));
            return;
        }
        // Protect container supporting the shop
        manager.getShopsView().values().forEach(s -> {
            if (s.getContainerLocation().equals(loc)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<red>Este contenedor pertenece a una tienda."));
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Block block = event.getClickedBlock();
        Shop shop = manager.get(block.getLocation());
        if (shop == null) return;
        event.setCancelled(true);

        Player player = event.getPlayer();
        if (player.getUniqueId().equals(shop.getOwner())) {
            ownerSessions.put(player.getUniqueId(), new OwnerSession(shop));
            player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Tienda de " + player.getName() + ". Escribe <green>retirar</green> para cobrar pagos, <red>borrar</red> para eliminar, <gray>cancelar</gray> para salir.")
                    .append(Component.space())
                    .append(buttons(
                            "<click:suggest_command:'retirar'><green>[Retirar]</green></click>",
                            "<click:suggest_command:'borrar'><red>[Borrar]</red></click>",
                            "<click:suggest_command:'cancelar'><gray>[Cancelar]</gray></click>"
                    )));
            return;
        }

        purchaseSessions.put(player.getUniqueId(), new PurchaseSession(shop));
        player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>¿Cuántos paquetes deseas comprar? (1, 10, 32, 64 o número). Escribe <gray>cancelar</gray> para salir.")
                .append(Component.space())
                .append(buttons(
                        "<click:suggest_command:'1'><white>[1]</white></click>",
                        "<click:suggest_command:'10'><white>[10]</white></click>",
                        "<click:suggest_command:'32'><white>[32]</white></click>",
                        "<click:suggest_command:'64'><white>[64]</white></click>",
                        "<click:suggest_command:'cancelar'><gray>[Cancelar]</gray></click>"
                )));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (!creationSessions.containsKey(id) && !purchaseSessions.containsKey(id) && !ownerSessions.containsKey(id)) {
            return;
        }

        event.setCancelled(true);
        String message = event.getMessage();
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (creationSessions.containsKey(id)) {
                handleCreationChat(player, message);
            } else if (purchaseSessions.containsKey(id)) {
                handlePurchaseChat(player, message);
            } else if (ownerSessions.containsKey(id)) {
                handleOwnerChat(player, message);
            }
        });
    }

    private void handleCreationChat(Player player, String message) {
        CreationSession session = creationSessions.get(player.getUniqueId());
        if (session == null) return;
        String msg = message.trim().toLowerCase(Locale.ROOT);
        if (msg.equals("cancelar")) {
            creationSessions.remove(player.getUniqueId());
            player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Creación cancelada."));
            return;
        }

        switch (session.stage) {
            case WAIT_ITEM -> {
                if (!msg.equals("ok")) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Escribe ok con el item en mano.")
                            .append(Component.space())
                            .append(buttons(
                                    "<click:suggest_command:'ok'><green>[OK]</green></click>",
                                    "<click:suggest_command:'cancelar'><gray>[Cancelar]</gray></click>"
                            )));
                    return;
                }
                ItemDescriptor desc = ItemDescriptor.fromItem(plugin, player.getInventory().getItemInMainHand());
                if (desc == null) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Necesitas un item en la mano."));
                    return;
                }
                session.sell = desc;
                player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Cantidad a vender por operación. Escribe un número o <green>mano</green> para usar la cantidad actual.")
                        .append(Component.space())
                        .append(buttons(
                                "<click:suggest_command:'mano'><green>[Mano]</green></click>",
                                "<click:suggest_command:'1'><white>[1]</white></click>",
                                "<click:suggest_command:'64'><white>[64]</white></click>",
                                "<click:suggest_command:'cancelar'><gray>[Cancelar]</gray></click>"
                        )));
                session.stage = CreationStage.WAIT_ITEM_AMOUNT;
            }
            case WAIT_ITEM_AMOUNT -> {
                int amount;
                if (msg.equals("mano")) {
                    amount = player.getInventory().getItemInMainHand().getAmount();
                } else {
                    try {
                        amount = Integer.parseInt(msg);
                    } catch (NumberFormatException ex) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Cantidad inválida."));
                        return;
                    }
                }
                if (amount <= 0) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Debe ser mayor a 0."));
                    return;
                }
                session.sellAmount = amount;
                player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Ahora sostén el item de pago y escribe <green>ok</green> (opcional número para la cantidad a cobrar).")
                    .append(Component.space())
                    .append(buttons(
                            "<click:suggest_command:'ok'><green>[OK]</green></click>",
                            "<click:suggest_command:'ok 64'><white>[OK 64]</white></click>",
                            "<click:suggest_command:'cancelar'><gray>[Cancelar]</gray></click>"
                    )));
                session.stage = CreationStage.WAIT_PRICE;
            }
            case WAIT_PRICE -> {
                if (!msg.startsWith("ok")) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Escribe ok con el item de pago en mano.")
                        .append(Component.space())
                        .append(buttons(
                                "<click:suggest_command:'ok'><green>[OK]</green></click>",
                                "<click:suggest_command:'cancelar'><gray>[Cancelar]</gray></click>"
                        )));
                    return;
                }
                String[] split = message.trim().split(" ");
                int amount = player.getInventory().getItemInMainHand().getAmount();
                if (split.length >= 2) {
                    try {
                        amount = Integer.parseInt(split[1]);
                    } catch (NumberFormatException ex) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Cantidad inválida."));
                        return;
                    }
                }
                if (amount <= 0) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Debe ser mayor a 0."));
                    return;
                }
                ItemDescriptor price = ItemDescriptor.fromItem(plugin, player.getInventory().getItemInMainHand());
                if (price == null) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Necesitas un item en la mano."));
                    return;
                }
                session.price = price;
                session.priceAmount = amount;
                session.stage = CreationStage.WAIT_CONFIRM;
                player.sendMessage(MiniMessage.miniMessage().deserialize("<gold>Vas a vender <white>" + session.sellAmount + "x</white> " + session.sell.format().content() + " por <white>" + amount + "x</white> " + price.format().content() + ". Escribe <green>confirmar</green> o <red>cancelar</red>.")
                    .append(Component.space())
                    .append(buttons(
                            "<click:suggest_command:'confirmar'><green>[Confirmar]</green></click>",
                            "<click:suggest_command:'cancelar'><gray>[Cancelar]</gray></click>"
                    )));
            }
            case WAIT_CONFIRM -> {
                if (!msg.equals("confirmar")) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Creación cancelada."));
                    creationSessions.remove(player.getUniqueId());
                    return;
                }
                Shop shop = new Shop(player.getUniqueId(), session.signLoc, session.containerLoc, session.sell, session.sellAmount, session.price, session.priceAmount);
                manager.create(shop);
                creationSessions.remove(player.getUniqueId());
                updateSign(session.signLoc, player.getName(), session);
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Tienda creada."));
            }
        }
    }

    private void handlePurchaseChat(Player player, String message) {
        PurchaseSession session = purchaseSessions.get(player.getUniqueId());
        if (session == null) return;
        String msg = message.trim().toLowerCase(Locale.ROOT);
        if (msg.equals("cancelar")) {
            purchaseSessions.remove(player.getUniqueId());
            player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Compra cancelada."));
            return;
        }
        int packs;
        try {
            packs = Integer.parseInt(msg);
        } catch (NumberFormatException ex) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Ingresa un número."));
            return;
        }
        if (packs <= 0) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Debe ser mayor a 0."));
            return;
        }

        Shop shop = session.shop;
        Inventory container = getContainerInventory(shop);
        if (container == null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>La tienda no tiene contenedor válido."));
            purchaseSessions.remove(player.getUniqueId());
            return;
        }

        int requiredSell = shop.getSellAmount() * packs;
        int requiredPrice = shop.getPriceAmount() * packs;

        if (countMatches(container, shop.getSellItem()) < requiredSell) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>La tienda no tiene stock suficiente."));
            purchaseSessions.remove(player.getUniqueId());
            return;
        }

        if (countMatches(player.getInventory(), shop.getPriceItem()) < requiredPrice) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>No tienes suficientes items de pago."));
            purchaseSessions.remove(player.getUniqueId());
            return;
        }

        List<ItemStack> soldStacks = takeItems(container, shop.getSellItem(), requiredSell);
        giveStacks(player, soldStacks);

        List<ItemStack> paymentStacks = takeItems(player.getInventory(), shop.getPriceItem(), requiredPrice);
        shop.addPayments(paymentStacks);
        manager.save();
        purchaseSessions.remove(player.getUniqueId());
        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Compra realizada."));
    }

    private void handleOwnerChat(Player player, String message) {
        OwnerSession session = ownerSessions.get(player.getUniqueId());
        if (session == null) return;
        String msg = message.trim().toLowerCase(Locale.ROOT);
        if (msg.equals("cancelar")) {
            ownerSessions.remove(player.getUniqueId());
            player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>Acción cancelada."));
            return;
        }
        if (msg.equals("retirar")) {
            payout(player, session.shop);
            ownerSessions.remove(player.getUniqueId());
            return;
        }
        if (msg.equals("borrar")) {
            payout(player, session.shop);
            manager.remove(session.shop);
            Block b = session.shop.getSignLocation().getBlock();
            b.setType(Material.AIR);
            ownerSessions.remove(player.getUniqueId());
            player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Tienda eliminada."));
            return;
        }
        player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Opciones: retirar / borrar / cancelar")
                .append(Component.space())
                .append(buttons(
                        "<click:suggest_command:'retirar'><green>[Retirar]</green></click>",
                        "<click:suggest_command:'borrar'><red>[Borrar]</red></click>",
                        "<click:suggest_command:'cancelar'><gray>[Cancelar]</gray></click>"
                )));
    }

    private void payout(Player player, Shop shop) {
        List<ItemStack> wallet = new ArrayList<>(shop.getWallet());
        if (wallet.isEmpty()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>No hay pagos pendientes."));
            return;
        }
        wallet.forEach(item -> giveOrDrop(player, item.clone()));
        shop.clearWallet();
        manager.save();
        player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Pagos cobrados."));
    }

    private void updateSign(Location signLoc, String owner, CreationSession session) {
        BlockState state = signLoc.getBlock().getState();
        if (state instanceof Sign sign) {
            sign.line(0, Component.text("[TIENDA]", NamedTextColor.GOLD));
            sign.line(1, Component.text(owner, NamedTextColor.WHITE));
            sign.line(2, Component.text("Vende " + session.sellAmount, NamedTextColor.GREEN));
            sign.line(3, Component.text("Por " + session.priceAmount, NamedTextColor.AQUA));
            sign.update(true, false);
        }
    }

    private Block getAttachedContainer(Block signBlock) {
        BlockData data = signBlock.getBlockData();
        if (data instanceof WallSign wall) {
            Block attached = signBlock.getRelative(wall.getFacing().getOppositeFace());
            if (isContainer(attached)) return attached;
        } else if (data instanceof Directional directional) {
            Block attached = signBlock.getRelative(directional.getFacing().getOppositeFace());
            if (isContainer(attached)) return attached;
        }
        return null;
    }

    private boolean isContainer(Block block) {
        BlockState state = block.getState();
        return state instanceof InventoryHolder;
    }

    private Inventory getContainerInventory(Shop shop) {
        BlockState state = shop.getContainerLocation().getBlock().getState();
        if (state instanceof InventoryHolder holder) {
            return holder.getInventory();
        }
        return null;
    }

    private int countMatches(Inventory inventory, ItemDescriptor desc) {
        int total = 0;
        for (ItemStack stack : inventory.getContents()) {
            if (matches(stack, desc)) {
                total += stack.getAmount();
            }
        }
        return total;
    }

    private List<ItemStack> takeItems(Inventory inventory, ItemDescriptor desc, int amount) {
        int remaining = amount;
        List<ItemStack> taken = new ArrayList<>();
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if (!matches(stack, desc)) continue;
            int take = Math.min(stack.getAmount(), remaining);
            ItemStack extracted = stack.clone();
            extracted.setAmount(take);
            taken.add(extracted);
            stack.setAmount(stack.getAmount() - take);
            if (stack.getAmount() <= 0) contents[i] = null;
            remaining -= take;
            if (remaining <= 0) break;
        }
        inventory.setContents(contents);
        return taken;
    }

    private void giveStacks(Player player, List<ItemStack> stacks) {
        if (stacks == null) return;
        stacks.forEach(stack -> giveOrDrop(player, stack));
    }

    private void giveOrDrop(Player player, ItemStack stack) {
        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(stack);
        if (!leftovers.isEmpty()) {
            leftovers.values().forEach(it -> player.getWorld().dropItemNaturally(player.getLocation(), it));
        }
    }

    private boolean matches(ItemStack stack, ItemDescriptor desc) {
        if (stack == null || stack.getType() == Material.AIR || desc == null) return false;
        if (!stack.getType().name().equalsIgnoreCase(desc.material())) return false;
        if (!enchantMap(stack).equals(desc.enchants())) return false;
        String cid = customId(stack);
        return (cid == null && desc.customId() == null) || (cid != null && cid.equals(desc.customId()));
    }

    private Map<String, Integer> enchantMap(ItemStack stack) {
        Map<String, Integer> map = new HashMap<>();
        if (stack == null) return map;
        stack.getEnchantments().forEach((ench, lvl) -> map.put(ench.getKey().getKey(), lvl));
        return map;
    }

    private String customId(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return null;
        ItemMeta meta = stack.getItemMeta();
        return meta.getPersistentDataContainer().get(new org.bukkit.NamespacedKey(plugin, "custom_item_id"), PersistentDataType.STRING);
    }

    private enum CreationStage {
        WAIT_ITEM,
        WAIT_ITEM_AMOUNT,
        WAIT_PRICE,
        WAIT_CONFIRM
    }

    private static class CreationSession {
        final Location signLoc;
        final Location containerLoc;
        CreationStage stage = CreationStage.WAIT_ITEM;
        ItemDescriptor sell;
        int sellAmount;
        ItemDescriptor price;
        int priceAmount;

        CreationSession(Location signLoc, Location containerLoc) {
            this.signLoc = signLoc;
            this.containerLoc = containerLoc;
        }
    }

    private record PurchaseSession(Shop shop) { }

    private record OwnerSession(Shop shop) { }

    private Component buttons(String... miniMessages) {
        Component combined = Component.empty();
        for (int i = 0; i < miniMessages.length; i++) {
            combined = combined.append(MiniMessage.miniMessage().deserialize(miniMessages[i]));
            if (i < miniMessages.length - 1) {
                combined = combined.append(Component.space());
            }
        }
        return combined;
    }
}
