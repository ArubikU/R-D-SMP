package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class EarthquakeModifier extends Modifier {

    private BukkitRunnable task;
    private final Random random = new Random();

    public EarthquakeModifier(RollAndDeathSMP plugin) {
        super(plugin, "Terremoto", ModifierType.CHAOS, "La pantalla tiembla cada cierto tiempo.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (random.nextDouble() < 0.05) { // 5% chance every second
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPLODE, 0.5f, 0.5f);
                        if (Math.random() < 0.1) {
                            player.sendMessage(MiniMessage.miniMessage().deserialize("<red>¡Terremoto!"));
                        }
                        player.setVelocity(player.getVelocity().add(new Vector(
                                (random.nextDouble() - 0.5) * 0.2,
                                0.1,
                                (random.nextDouble() - 0.5) * 0.2
                        )));
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
