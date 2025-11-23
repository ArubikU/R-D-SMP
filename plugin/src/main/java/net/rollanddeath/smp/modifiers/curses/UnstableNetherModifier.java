package net.rollanddeath.smp.modifiers.curses;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class UnstableNetherModifier extends Modifier {

    private final Random random = new Random();

    public UnstableNetherModifier(JavaPlugin plugin) {
        super(plugin, "Nether Inestable", ModifierType.CURSE, "Los portales te llevan a coordenadas random en el Nether.");
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            Location to = event.getTo();
            if (to != null && to.getWorld() != null && to.getWorld().getEnvironment() == World.Environment.NETHER) {
                World nether = to.getWorld();
                int x = random.nextInt(2000) - 1000; // +/- 1000
                int z = random.nextInt(2000) - 1000;
                int y = 50; // Start search at 50
                
                // Find safe spot (simple version, might spawn in wall if not careful, but Paper usually handles safe spawn search if we just set location)
                // Actually PlayerPortalEvent uses the portal logic to find a portal.
                // If we change the location, it will try to find/create a portal there.
                // That's exactly what we want.
                
                Location randomLoc = new Location(nether, x, y, z);
                event.setTo(randomLoc);
                event.getPlayer().sendMessage(Component.text("¡El portal es inestable!", NamedTextColor.RED));
            }
        }
    }
}
