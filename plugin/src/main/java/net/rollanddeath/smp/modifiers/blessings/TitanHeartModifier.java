package net.rollanddeath.smp.modifiers.blessings;

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
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TitanHeartModifier extends Modifier {

    private final AttributeModifier healthBoost;

    public TitanHeartModifier(JavaPlugin plugin) {
        super(plugin, "Corazón de Titán", ModifierType.BLESSING, "+2 Corazones de vida máxima permanentes.");
        this.healthBoost = new AttributeModifier(new NamespacedKey(plugin, "titan_heart"), 4.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY);
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
        if (attribute != null && !attribute.getModifiers().contains(healthBoost)) {
            attribute.addModifier(healthBoost);
        }
    }

    private void removeHeart(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (attribute != null && attribute.getModifiers().contains(healthBoost)) {
            attribute.removeModifier(healthBoost);
        }
    }
}
