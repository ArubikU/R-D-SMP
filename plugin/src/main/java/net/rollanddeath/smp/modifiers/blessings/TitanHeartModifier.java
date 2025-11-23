package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class TitanHeartModifier extends Modifier {

    private static final UUID MODIFIER_UUID = UUID.fromString("b2b2b2b2-b2b2-b2b2-b2b2-b2b2b2b2b2b2");
    private static final AttributeModifier HEALTH_BOOST = new AttributeModifier(MODIFIER_UUID, "Titan Heart", 4.0, AttributeModifier.Operation.ADD_NUMBER);

    public TitanHeartModifier(JavaPlugin plugin) {
        super(plugin, "Corazón de Titán", ModifierType.BLESSING, "+2 Corazones de vida máxima permanentes.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for (Player player : Bukkit.getOnlinePlayers()) {
            addHeart(player);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            removeHeart(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        addHeart(event.getPlayer());
    }

    private void addHeart(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (attribute != null && !attribute.getModifiers().contains(HEALTH_BOOST)) {
            attribute.addModifier(HEALTH_BOOST);
        }
    }

    private void removeHeart(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (attribute != null && attribute.getModifiers().contains(HEALTH_BOOST)) {
            attribute.removeModifier(HEALTH_BOOST);
        }
    }
}
