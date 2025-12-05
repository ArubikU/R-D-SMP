package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class MagicArcherModifier extends Modifier {

    private BukkitTask arrowTask;

    public MagicArcherModifier(RollAndDeathSMP plugin) {
        super(plugin, "Arquero Mágico", ModifierType.BLESSING, "Las flechas no se consumen.");
    }

    @Override
    public void onEnable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        // Ensure players always have at least one arrow so bows can fire even with empty inventory.
        arrowTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getInventory().contains(Material.ARROW)) continue;
                player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
            }
        }, 20L, 40L);
    }

    @Override
    public void onDisable() {
        if (arrowTask != null) {
            arrowTask.cancel();
            arrowTask = null;
        }
        org.bukkit.event.HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setConsumeItem(false);
            // Optional: Update inventory to sync client? Usually not needed for setConsumeItem(false)
        }
    }
}
