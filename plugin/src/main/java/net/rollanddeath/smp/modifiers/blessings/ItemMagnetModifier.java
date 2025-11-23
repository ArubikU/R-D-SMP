package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemMagnetModifier extends Modifier {

    private BukkitRunnable task;

    public ItemMagnetModifier(JavaPlugin plugin) {
        super(plugin, "Imán de Items", ModifierType.BLESSING, "Los items del suelo vuelan hacia ti (rango 5m).");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
                        if (entity instanceof Item item) {
                            if (item.getPickupDelay() > 0) continue;
                            
                            Vector direction = player.getLocation().toVector().subtract(item.getLocation().toVector()).normalize();
                            item.setVelocity(direction.multiply(0.5));
                        }
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 5L, 5L);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        if (task != null) task.cancel();
    }
}
