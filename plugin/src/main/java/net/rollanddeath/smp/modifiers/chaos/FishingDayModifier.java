package net.rollanddeath.smp.modifiers.chaos;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class FishingDayModifier extends Modifier {

    public FishingDayModifier(RollAndDeathSMP plugin) {
        super(plugin, "Día de Pesca", ModifierType.CHAOS, "Solo se puede comer pescado.");
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (event.getItem() != null && event.getItem().getType().isEdible()) {
            if (event.getItem().getType() != Material.COOKED_COD && event.getItem().getType() != Material.COOKED_SALMON &&
                event.getItem().getType() != Material.COD && event.getItem().getType() != Material.SALMON &&
                event.getItem().getType() != Material.TROPICAL_FISH && event.getItem().getType() != Material.PUFFERFISH) {
                
                event.setCancelled(true);
                event.getPlayer().sendMessage(MiniMessage.miniMessage().deserialize("<red>¡Solo puedes comer pescado!"));
            }
        }
    }
}
