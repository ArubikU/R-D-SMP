package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

public class HeavyInventoryModifier extends Modifier {

    private BukkitTask task;

    public HeavyInventoryModifier(JavaPlugin plugin) {
        super(plugin, "Inventario Pesado", ModifierType.CURSE, "Si llevas el inventario lleno, tienes Lentitud I.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::checkPlayers, 20L, 20L);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    private void checkPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isInventoryFull(player)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0, false, false));
            }
        }
    }

    private boolean isInventoryFull(Player player) {
        // Check main inventory contents (excluding armor/offhand usually, but getContents includes them?)
        // getStorageContents is main inventory + hotbar.
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.getType().isAir()) {
                return false; // Found an empty slot
            }
        }
        return true;
    }
}
