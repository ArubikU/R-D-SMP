package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InvisibilityCloak extends CustomItem {

    private final Map<UUID, Long> lastMoveTime = new HashMap<>();

    public InvisibilityCloak(RollAndDeathSMP plugin) {
        super(plugin, "INVISIBILITY_CLOAK");
        // Task to check for invisibility
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ItemStack chest = player.getInventory().getChestplate();
                if (isItem(chest)) {
                    long lastMove = lastMoveTime.getOrDefault(player.getUniqueId(), System.currentTimeMillis());
                    if (System.currentTimeMillis() - lastMove > 3000) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0, false, false, false));
                    }
                }
            }
        }, 20L, 20L);
    }

    @Override
    protected ItemStack createBaseItem() {
        ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.BLACK);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public String getDisplayName() {
        return "Capa de Invisibilidad";
    }

    @Override
    protected Integer getCustomModelData() {
        return 710018;
    }

    @Override
    protected List<String> getLore() {
        return List.of("Te hace invisible a mobs si no te mueves.");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
            event.getFrom().getBlockY() != event.getTo().getBlockY() ||
            event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            lastMoveTime.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }
}
