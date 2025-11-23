package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PermanentPotionModifier extends Modifier {

    public PermanentPotionModifier(RollAndDeathSMP plugin) {
        super(plugin, "Poción Permanente", ModifierType.BLESSING, "Efecto de Haste I para todos.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        // Apply to all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            applyEffect(player);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // Remove from all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.removePotionEffect(PotionEffectType.HASTE);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        applyEffect(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        applyEffect(event.getPlayer());
    }

    private void applyEffect(Player player) {
        // Haste I (Amplifier 0), Infinite duration
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, PotionEffect.INFINITE_DURATION, 0, false, false));
    }
}
