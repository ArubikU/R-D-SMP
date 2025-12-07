package net.rollanddeath.smp.core.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ArmorRestrictionTask extends BukkitRunnable implements Listener {

    private final RollAndDeathSMP plugin;
    private final GameManager gameManager;

    public ArmorRestrictionTask(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            enforceArmorRules(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        enforceArmorRules(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> enforceArmorRules(event.getPlayer()));
    }

    private void enforceArmorRules(Player player) {
        if (gameManager == null) {
            return;
        }

        int day = Math.max(1, gameManager.getCurrentDay());
        int allowedDiamond = Math.min(4, Math.max(0, day - 1));
        int allowedNetherite = Math.min(4, Math.max(0, day - 4));

        ItemStack[] armor = player.getInventory().getArmorContents();
        List<ItemStack> toDrop = new ArrayList<>();
        int diamondUsed = 0;
        int netheriteUsed = 0;

        for (int i = 0; i < armor.length; i++) {
            ItemStack piece = armor[i];
            if (piece == null || piece.getType() == Material.AIR) {
                continue;
            }

            Material type = piece.getType();
            if (isDiamondArmor(type)) {
                if (diamondUsed < allowedDiamond) {
                    diamondUsed++;
                } else {
                    toDrop.add(piece);
                    armor[i] = null;
                }
            } else if (isNetheriteArmor(type)) {
                if (netheriteUsed < allowedNetherite) {
                    netheriteUsed++;
                } else {
                    toDrop.add(piece);
                    armor[i] = null;
                }
            }
        }

        player.getInventory().setArmorContents(armor);
        if (!toDrop.isEmpty()) {
            toDrop.forEach(item -> {
                var leftover = player.getInventory().addItem(item);
                leftover.values().forEach(rem -> player.getWorld().dropItemNaturally(player.getLocation(), rem));
            });
            player.sendMessage(Component.text(
                    "Restricción de armaduras día " + day + ": Diamante máximo " + allowedDiamond + ", Netherita máximo " + allowedNetherite + ".", 
                    NamedTextColor.RED));
        }
    }

    private boolean isDiamondArmor(Material type) {
        return type == Material.DIAMOND_HELMET || type == Material.DIAMOND_CHESTPLATE
                || type == Material.DIAMOND_LEGGINGS || type == Material.DIAMOND_BOOTS;
    }

    private boolean isNetheriteArmor(Material type) {
        return type == Material.NETHERITE_HELMET || type == Material.NETHERITE_CHESTPLATE
                || type == Material.NETHERITE_LEGGINGS || type == Material.NETHERITE_BOOTS;
    }
}
