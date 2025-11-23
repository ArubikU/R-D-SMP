package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ExtraLifeModifier extends Modifier {

    private final NamespacedKey modifierKey;

    public ExtraLifeModifier(RollAndDeathSMP plugin) {
        super(plugin, "Vida Extra", ModifierType.BLESSING, "+2 Corazones de vida máxima.");
        this.modifierKey = new NamespacedKey(plugin, "extra_life");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for (Player player : Bukkit.getOnlinePlayers()) {
            applyModifier(player);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (Player player : Bukkit.getOnlinePlayers()) {
            removeModifier(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        applyModifier(event.getPlayer());
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeModifier(event.getPlayer());
    }

    private void applyModifier(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (attribute != null) {
            if (!hasModifier(attribute)) {
                attribute.addModifier(new AttributeModifier(modifierKey, 4.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
            }
        }
    }

    private void removeModifier(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (attribute != null) {
            for (AttributeModifier modifier : attribute.getModifiers()) {
                if (modifier.getKey().equals(modifierKey)) {
                    attribute.removeModifier(modifier);
                }
            }
        }
    }
    
    private boolean hasModifier(AttributeInstance attribute) {
        for (AttributeModifier modifier : attribute.getModifiers()) {
            if (modifier.getKey().equals(modifierKey)) {
                return true;
            }
        }
        return false;
    }
}
