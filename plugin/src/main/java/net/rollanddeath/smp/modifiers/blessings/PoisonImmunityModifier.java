package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.java.JavaPlugin;

public class PoisonImmunityModifier extends Modifier {

    public PoisonImmunityModifier(JavaPlugin plugin) {
        super(plugin, "Inmunidad al Veneno", ModifierType.BLESSING, "Inmune a veneno y wither.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.removePotionEffect(PotionEffectType.POISON);
            player.removePotionEffect(PotionEffectType.WITHER);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player) {
            PotionEffectType type = event.getNewEffect() != null ? event.getNewEffect().getType() : null;
            if (type == PotionEffectType.POISON || type == PotionEffectType.WITHER) {
                event.setCancelled(true);
            }
        }
    }
}
