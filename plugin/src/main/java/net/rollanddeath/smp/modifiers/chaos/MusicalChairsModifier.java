package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class MusicalChairsModifier extends Modifier {

    private BukkitRunnable task;

    public MusicalChairsModifier(RollAndDeathSMP plugin) {
        super(plugin, "Juego de la Silla", ModifierType.CHAOS, "Cada 30 min, todos cambian de posición con otro jugador.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                if (players.size() < 2) return;

                Collections.shuffle(players);
                
                // Store locations first
                List<Location> locations = new ArrayList<>();
                for (Player p : players) {
                    locations.add(p.getLocation());
                }

                // Rotate locations: P1 -> Loc2, P2 -> Loc3 ... Pn -> Loc1
                for (int i = 0; i < players.size(); i++) {
                    Player p = players.get(i);
                    Location newLoc = locations.get((i + 1) % players.size());
                    p.teleport(newLoc);
                    p.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>¡Cambio de lugar! Has sido teletransportado."));
                }
            }
        };
        // 30 minutes = 30 * 60 * 20 = 36000 ticks
        task.runTaskTimer(plugin, 36000L, 36000L);
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
