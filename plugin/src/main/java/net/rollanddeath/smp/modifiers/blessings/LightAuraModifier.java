package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

public class LightAuraModifier extends Modifier {

    private BukkitRunnable task;

    public LightAuraModifier(JavaPlugin plugin) {
        super(plugin, "Aura de Luz", ModifierType.BLESSING, "Visión nocturna permanente.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        applyEffectToAll();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                applyEffectToAll();
            }
        };
        task.runTaskTimer(plugin, 100L, 100L);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        if (task != null) {
            task.cancel();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        applyEffect(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                applyEffect(event.getPlayer());
            }
        }.runTaskLater(plugin, 1L);
    }

    private void applyEffectToAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            applyEffect(player);
        }
    }

    private void applyEffect(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
        }
    }
}
