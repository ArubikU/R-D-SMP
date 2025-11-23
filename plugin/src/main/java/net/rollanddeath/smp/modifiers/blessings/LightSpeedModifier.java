package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

public class LightSpeedModifier extends Modifier {

    private BukkitRunnable task;

    public LightSpeedModifier(JavaPlugin plugin) {
        super(plugin, "Velocidad de la Luz", ModifierType.BLESSING, "Caminar sobre caminos de tierra da Velocidad II.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Block block = player.getLocation().getBlock();
                    Block below = block.getRelative(0, -1, 0);
                    
                    if (block.getType() == Material.DIRT_PATH || below.getType() == Material.DIRT_PATH) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1, false, false));
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 10L, 10L); // Check every 0.5s
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        if (task != null) {
            task.cancel();
        }
    }
}
