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

public class GillsModifier extends Modifier {

    private BukkitRunnable task;

    public GillsModifier(JavaPlugin plugin) {
        super(plugin, "Branquias", ModifierType.BLESSING, "Respiración acuática infinita.");
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
            player.removePotionEffect(PotionEffectType.WATER_BREATHING);
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
        if (!player.hasPotionEffect(PotionEffectType.WATER_BREATHING)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0, false, false));
        }
    }
}
