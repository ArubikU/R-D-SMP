package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TacticalInvisibilityModifier extends Modifier {

    private final Map<UUID, Integer> sneakTime = new HashMap<>();
    private BukkitRunnable task;

    public TacticalInvisibilityModifier(RollAndDeathSMP plugin) {
        super(plugin, "Invisibilidad Táctica", ModifierType.BLESSING, "Si te agachas por 3 segundos, te vuelves invisible.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.isSneaking()) {
                        int time = sneakTime.getOrDefault(player.getUniqueId(), 0);
                        time++;
                        sneakTime.put(player.getUniqueId(), time);

                        if (time >= 3) { // 3 seconds (assuming run every 20 ticks/1s)
                            // Apply Invisibility for short duration so it expires if they stop sneaking (or we remove it)
                            // Actually, let's apply for 2 seconds, so it refreshes.
                            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 0, false, false));
                        }
                    } else {
                        sneakTime.remove(player.getUniqueId());
                        // Optional: Remove invisibility immediately if they stop sneaking?
                        // If we rely on short duration, it will expire.
                        // But if they have invisibility from potion, we shouldn't remove it?
                        // Let's just let it expire if we use short duration.
                        // Or we can check if they have the effect and remove it if it's ours.
                        // For simplicity, let's just let it expire.
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 20L); // Run every second
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null) {
            task.cancel();
            task = null;
        }
        sneakTime.clear();
    }
}
