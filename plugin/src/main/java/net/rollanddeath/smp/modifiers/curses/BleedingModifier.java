package net.rollanddeath.smp.modifiers.curses;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BleedingModifier extends Modifier {

    private final Map<UUID, Integer> bleedingPlayers = new HashMap<>();
    private BukkitRunnable task;

    public BleedingModifier(JavaPlugin plugin) {
        super(plugin, "Sangrado", ModifierType.CURSE, "Recibir daño puede causar sangrado (daño periódico).");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (bleedingPlayers.isEmpty()) return;

                for (UUID uuid : new HashMap<>(bleedingPlayers).keySet()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null || !player.isOnline() || player.isDead()) {
                        bleedingPlayers.remove(uuid);
                        continue;
                    }

                    int ticks = bleedingPlayers.get(uuid);
                    if (ticks <= 0) {
                        bleedingPlayers.remove(uuid);
                        continue;
                    }

                    // Damage every 2 seconds (40 ticks). Task runs every 10 ticks.
                    // Let's simplify: Task runs every 20 ticks (1s). Damage every 1s.
                    player.damage(1.0);
                    player.getWorld().spawnParticle(Particle.DUST, player.getLocation().add(0, 1, 0), 10, 0.2, 0.2, 0.2, new Particle.DustOptions(Color.RED, 1));
                    
                    bleedingPlayers.put(uuid, ticks - 1);
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 20L);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (task != null) task.cancel();
        bleedingPlayers.clear();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            // 20% chance to bleed on any damage (except bleeding itself to avoid loops if we used custom damage cause, but player.damage() uses GENERIC)
            // To avoid loop, check cause? player.damage() usually triggers EntityDamageEvent.
            // We should check if the damage is high enough maybe? Or just chance.
            // To avoid infinite loop from our own damage:
            // We can't easily distinguish player.damage(1.0) from other sources without metadata or custom event.
            // But 1.0 damage is low. Maybe ignore damage < 2.0?
            
            if (event.getDamage() < 1.5) return; // Ignore tiny damage

            if (Math.random() < 0.20) {
                bleedingPlayers.put(player.getUniqueId(), 10); // 10 seconds of bleeding
                player.sendMessage(Component.text("¡Estás sangrando!", NamedTextColor.RED));
            }
        }
    }
}
