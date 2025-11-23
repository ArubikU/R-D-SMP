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

public class SuperStrengthModifier extends Modifier {

    private static final UUID MODIFIER_UUID = UUID.fromString("c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3");
    private static final AttributeModifier STRENGTH_BOOST = new AttributeModifier(MODIFIER_UUID, "Super Strength", 0.2, AttributeModifier.Operation.ADD_SCALAR);

    public SuperStrengthModifier(JavaPlugin plugin) {
        super(plugin, "Fuerza Descomunal", ModifierType.BLESSING, "+20% de daño de ataque.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for (Player player : Bukkit.getOnlinePlayers()) {
            addStrength(player);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            removeStrength(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        addStrength(event.getPlayer());
    }

    private void addStrength(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.ATTACK_DAMAGE);
        if (attribute != null && !attribute.getModifiers().contains(STRENGTH_BOOST)) {
            attribute.addModifier(STRENGTH_BOOST);
        }
    }

    private void removeStrength(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.ATTACK_DAMAGE);
        if (attribute != null && attribute.getModifiers().contains(STRENGTH_BOOST)) {
            attribute.removeModifier(STRENGTH_BOOST);
        }
    }
}
