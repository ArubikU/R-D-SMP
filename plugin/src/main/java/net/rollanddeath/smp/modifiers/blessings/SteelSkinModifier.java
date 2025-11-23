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

public class SteelSkinModifier extends Modifier {

    private BukkitRunnable task;

    public SteelSkinModifier(JavaPlugin plugin) {
        super(plugin, "Piel de Acero", ModifierType.BLESSING, "Resistencia al fuego permanente.");
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
        task.runTaskTimer(plugin, 100L, 100L); // Check every 5 seconds
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        if (task != null) {
            task.cancel();
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        applyEffect(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        // Delay slightly to ensure effect applies after respawn
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
        if (!player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
        }
    }
}
