package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class RegenerativeBedModifier extends Modifier {

    public RegenerativeBedModifier(RollAndDeathSMP plugin) {
        super(plugin, "Cama Regenerativa", ModifierType.BLESSING, "Dormir cura toda la vida al instante.");
    }

    @Override
    public void onEnable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            Player player = event.getPlayer();
            double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
            if (player.getHealth() < maxHealth) {
                player.setHealth(maxHealth);
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>¡Has descansado y recuperado toda tu salud!"));
            }
        }
    }
}
