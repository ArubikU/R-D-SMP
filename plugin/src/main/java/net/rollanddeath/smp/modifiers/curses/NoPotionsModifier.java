package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NoPotionsModifier extends Modifier {

    public NoPotionsModifier(JavaPlugin plugin) {
        super(plugin, "Sin Pociones", ModifierType.CURSE, "Las pociones no tienen efecto.");
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.POTION) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("Las pociones están desactivadas.", NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onSplash(PotionSplashEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onCloud(AreaEffectCloudApplyEvent event) {
        event.setCancelled(true);
    }
}
