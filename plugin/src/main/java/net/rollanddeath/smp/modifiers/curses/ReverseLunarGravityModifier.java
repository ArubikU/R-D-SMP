package net.rollanddeath.smp.modifiers.curses;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ReverseLunarGravityModifier extends Modifier {

    public ReverseLunarGravityModifier(JavaPlugin plugin) {
        super(plugin, "Gravedad Lunar Inversa", ModifierType.CURSE, "Si saltas, flotas hacia arriba por 5 segundos.");
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 0));
    }
}
