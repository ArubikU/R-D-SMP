package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.plugin.java.JavaPlugin;

public class ReverseLunarGravityModifier extends Modifier {

    private final AttributeModifier gravityModifier;

    public ReverseLunarGravityModifier(JavaPlugin plugin) {
        super(plugin, "Gravedad Lunar Inversa", ModifierType.CURSE, "La gravedad se reduce en un 20%.");
        this.gravityModifier = new AttributeModifier(new NamespacedKey(plugin, "reverse_lunar_gravity"), -0.2, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.ANY);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            applyGravity(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        applyGravity(event.getPlayer());
    }

    @SuppressWarnings("deprecation")
    private void applyGravity(Player player) {
        try {
            Attribute gravityAttribute = Attribute.valueOf("GRAVITY");
            AttributeInstance attribute = player.getAttribute(gravityAttribute);
            if (attribute != null) {
                if (!attribute.getModifiers().contains(gravityModifier)) {
                    attribute.addModifier(gravityModifier);
                }
            }
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("GRAVITY attribute not found. Is the server version 1.21+?");
        }
    }
}
