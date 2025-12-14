package net.rollanddeath.smp.core.items.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.BanList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class SoulContract extends CustomItem {

    private final Map<UUID, Long> pendingRevives = new HashMap<>();

    public SoulContract(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.SOUL_CONTRACT);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.PAPER);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Ban 24h a cambio de revivir a otro.", "Click derecho para usar.");
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) return;
        if (!isItem(event.getItem())) return;
        
        Player player = event.getPlayer();
        if (!event.getAction().isRightClick()) return;

        event.setCancelled(true);
        
        pendingRevives.put(player.getUniqueId(), System.currentTimeMillis());
        player.sendMessage(Component.text("Escribe en el chat el nombre del jugador a revivir (o 'cancelar').", NamedTextColor.YELLOW));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!pendingRevives.containsKey(player.getUniqueId())) return;
        
        // Check timeout (30 seconds)
        if (System.currentTimeMillis() - pendingRevives.get(player.getUniqueId()) > 30000) {
            pendingRevives.remove(player.getUniqueId());
            return;
        }

        event.setCancelled(true);
        String message = event.getMessage();
        pendingRevives.remove(player.getUniqueId());

        if (message.equalsIgnoreCase("cancelar")) {
            player.sendMessage(Component.text("Operación cancelada.", NamedTextColor.RED));
            return;
        }

        // Find target
        OfflinePlayer target = Bukkit.getOfflinePlayer(message);
        
        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
             player.sendMessage(Component.text("Jugador no encontrado o nunca ha jugado.", NamedTextColor.RED));
             return;
        }

        // Execute revive on main thread
        Bukkit.getScheduler().runTask(plugin, () -> {
            Player onlineTarget = target.getPlayer();
            if (onlineTarget != null && !plugin.getLifeManager().isEliminated(onlineTarget)) {
                player.sendMessage(Component.text("Ese jugador no está muerto/eliminado.", NamedTextColor.RED));
                return;
            }

            // Consume item
            ItemStack hand = player.getInventory().getItemInMainHand();
            boolean consumed = false;
            if (isItem(hand)) {
                hand.setAmount(hand.getAmount() - 1);
                consumed = true;
            } else {
                 ItemStack offhand = player.getInventory().getItemInOffHand();
                 if (isItem(offhand)) {
                     offhand.setAmount(offhand.getAmount() - 1);
                     consumed = true;
                 }
            }
            
            if (!consumed) {
                player.sendMessage(Component.text("Debes tener el contrato en la mano.", NamedTextColor.RED));
                return;
            }

            plugin.getLifeManager().revive(target.getUniqueId());
            player.sendMessage(Component.text("Has revivido a " + target.getName(), NamedTextColor.GREEN));
            
            // Unban if banned
            Bukkit.getBanList(BanList.Type.NAME).pardon(target.getName());

            // Ban the user who used the contract for 24 hours
            Date expiration = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L);
            Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "Has firmado el Contrato de Alma. Nos vemos en 24 horas.", expiration, "RollAndDeath SMP");
            if (player.isOnline()) {
                player.kick(Component.text("Has firmado el Contrato de Alma.\n\nNos vemos en 24 horas.", NamedTextColor.DARK_RED));
            }
        });
    }
}
