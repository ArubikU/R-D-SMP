package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import org.bukkit.Material;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class XPMagnet extends CustomItem {

    public XPMagnet(RollAndDeathSMP plugin) {
        super(plugin, "XP_MAGNET");
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.EMERALD);
    }

    @Override
    public String getDisplayName() {
        return "Imán de XP";
    }

    @Override
    protected Integer getCustomModelData() {
        return 710012;
    }

    @Override
    protected List<String> getLore() {
        return List.of("Atrae experiencia desde 20 bloques.");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && 
            event.getFrom().getBlockY() == event.getTo().getBlockY() && 
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        boolean hasMagnet = false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (isItem(item)) {
                hasMagnet = true;
                break;
            }
        }
        if (!hasMagnet && isItem(player.getInventory().getItemInOffHand())) {
            hasMagnet = true;
        }

        if (hasMagnet) {
            for (org.bukkit.entity.Entity entity : player.getNearbyEntities(20, 20, 20)) {
                if (entity instanceof ExperienceOrb) {
                    ExperienceOrb orb = (ExperienceOrb) entity;
                    orb.teleport(player.getLocation());
                }
            }
        }
    }
}
