package net.rollanddeath.smp.modifiers.curses;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class NightmaresModifier extends Modifier {

    private final Random random = new Random();

    public NightmaresModifier(JavaPlugin plugin) {
        super(plugin, "Pesadillas", ModifierType.CURSE, "Dormir tiene un 50% de chance de spawnear un Phantom instantáneo.");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            if (random.nextBoolean()) { // 50% chance
                event.setCancelled(true);
                Player player = event.getPlayer();
                player.sendMessage(Component.text("¡Tienes una pesadilla!", NamedTextColor.RED));
                
                Location spawnLoc = player.getLocation().add(0, 2, 0);
                player.getWorld().spawnEntity(spawnLoc, EntityType.PHANTOM);
            }
        }
    }
}
