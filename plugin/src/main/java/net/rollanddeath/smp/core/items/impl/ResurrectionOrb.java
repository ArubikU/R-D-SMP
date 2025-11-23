package net.rollanddeath.smp.core.items.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class ResurrectionOrb extends CustomItem {

    private final Map<UUID, Long> pendingRevives = new HashMap<>();

    public ResurrectionOrb(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.RESURRECTION_ORB);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.HEART_OF_THE_SEA);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Revive a un compañero. Costo: 1 Vida propia (pagado al craftear).", "Click derecho para usar.");
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
                player.sendMessage(Component.text("Debes tener el orbe en la mano.", NamedTextColor.RED));
                return;
            }

            plugin.getLifeManager().revive(target.getUniqueId());
            player.sendMessage(Component.text("Has revivido a " + target.getName(), NamedTextColor.GREEN));
        });
    }
}
