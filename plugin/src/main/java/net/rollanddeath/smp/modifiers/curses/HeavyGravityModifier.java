package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.plugin.java.JavaPlugin;

public class HeavyGravityModifier extends Modifier {

    private final AttributeModifier jumpPenalty;

    public HeavyGravityModifier(JavaPlugin plugin) {
        super(plugin, "Gravedad Pesada", ModifierType.CURSE, "No se puede saltar bloques completos. Caída hace x2 daño.");
        // Reduce jump strength by ~40% to prevent jumping full blocks
        this.jumpPenalty = new AttributeModifier(new NamespacedKey(plugin, "heavy_gravity"), -0.4, AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.ANY);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for (Player p : Bukkit.getOnlinePlayers()) {
            applyGravity(p);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        for (Player p : Bukkit.getOnlinePlayers()) {
            removeGravity(p);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        applyGravity(event.getPlayer());
    }

    private void applyGravity(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.JUMP_STRENGTH);
        if (attribute != null && !attribute.getModifiers().contains(jumpPenalty)) {
            attribute.addModifier(jumpPenalty);
        }
    }

    private void removeGravity(Player player) {
        AttributeInstance attribute = player.getAttribute(Attribute.JUMP_STRENGTH);
        if (attribute != null && attribute.getModifiers().contains(jumpPenalty)) {
            attribute.removeModifier(jumpPenalty);
        }
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setDamage(event.getDamage() * 2);
        }
    }
}
