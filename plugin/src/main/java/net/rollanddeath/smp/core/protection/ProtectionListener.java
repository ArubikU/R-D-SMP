package net.rollanddeath.smp.core.protection;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;

public class ProtectionListener implements Listener {

    private final ProtectionManager protectionManager;
    private final Set<Material> protectableTypes = Set.of(
            Material.CHEST, Material.TRAPPED_CHEST, Material.BARREL, 
            Material.FURNACE, Material.BLAST_FURNACE, Material.SMOKER,
            Material.SHULKER_BOX, Material.HOPPER, Material.DISPENSER, Material.DROPPER,
            Material.SPAWNER
    );

    public ProtectionListener(ProtectionManager protectionManager) {
        this.protectionManager = protectionManager;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (protectableTypes.contains(block.getType())) {
            protectionManager.protectBlock(event.getPlayer(), block);
            event.getPlayer().sendMessage(Component.text("Bloque protegido automáticamente.", NamedTextColor.GREEN));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (protectionManager.isProtected(block)) {
            // Check access rights
            if (!protectionManager.canAccess(event.getPlayer(), block)) {
                // Offline Protection Check
                if (!protectionManager.isOwnerOnline(block)) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Component.text("¡Protección Reforzada! El dueño está desconectado.", NamedTextColor.RED));
                    return;
                }
                
                // If owner is online, maybe allow breaking if it's war? 
                // For now, strict protection unless bypass
                event.setCancelled(true);
                event.getPlayer().sendMessage(Component.text("Este bloque está protegido.", NamedTextColor.RED));
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) return;
        Block block = event.getClickedBlock();
        if (block == null) return;

        if (protectionManager.isProtected(block)) {
            if (!protectionManager.canAccess(event.getPlayer(), block)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Component.text("Este bloque está protegido.", NamedTextColor.RED));
            }
        }
    }
}
