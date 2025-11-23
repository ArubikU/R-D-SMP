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

public class SuperJumpModifier extends Modifier {

    private BukkitRunnable task;

    public SuperJumpModifier(JavaPlugin plugin) {
        super(plugin, "Supersalto", ModifierType.BLESSING, "Salto mejorado II permanente.");
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
            player.removePotionEffect(PotionEffectType.JUMP);
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
        if (!player.hasPotionEffect(PotionEffectType.JUMP)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1, false, false));
        }
    }
}
