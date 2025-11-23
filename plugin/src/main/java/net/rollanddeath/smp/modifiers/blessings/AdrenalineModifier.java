package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.java.JavaPlugin;

public class AdrenalineModifier extends Modifier {

    public AdrenalineModifier(JavaPlugin plugin) {
        super(plugin, "Adrenalina", ModifierType.BLESSING, "Bajo 3 corazones = Velocidad III.");
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            // Calculate health after damage
            double finalHealth = player.getHealth() - event.getFinalDamage();
            checkAdrenaline(player, finalHealth);
        }
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player player) {
            double finalHealth = player.getHealth() + event.getAmount();
            checkAdrenaline(player, finalHealth);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        checkAdrenaline(event.getPlayer(), event.getPlayer().getHealth());
    }

    private void checkAdrenaline(Player player, double health) {
        if (health <= 6.0 && health > 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, false, false));
        } else {
            if (player.hasPotionEffect(PotionEffectType.SPEED) && player.getPotionEffect(PotionEffectType.SPEED).getAmplifier() == 2) {
                player.removePotionEffect(PotionEffectType.SPEED);
            }
        }
    }
}
