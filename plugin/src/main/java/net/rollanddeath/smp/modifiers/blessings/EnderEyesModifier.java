package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EnderEyesModifier extends Modifier {

    public EnderEyesModifier(RollAndDeathSMP plugin) {
        super(plugin, "Ojos de Ender", ModifierType.BLESSING, "Endermans dropean perlas 100% garantizado.");
    }

    @Override
    public void onEnable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.ENDERMAN) {
            // Asegurar al menos 1 perla
            boolean hasPearl = false;
            for (ItemStack drop : event.getDrops()) {
                if (drop.getType() == Material.ENDER_PEARL) {
                    hasPearl = true;
                    break;
                }
            }
            
            if (!hasPearl) {
                event.getDrops().add(new ItemStack(Material.ENDER_PEARL, 1));
            }
        }
    }
}
