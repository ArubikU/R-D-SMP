package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class DoubleLootModifier extends Modifier {

    public DoubleLootModifier(JavaPlugin plugin) {
        super(plugin, "Doble Loot", ModifierType.BLESSING, "Mobs sueltan el doble de ítems.");
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return; // Don't double player loot
        if (event.getEntity().getKiller() == null) return; // Only if killed by player

        List<ItemStack> drops = event.getDrops();
        List<ItemStack> extraDrops = new ArrayList<>();

        for (ItemStack drop : drops) {
            extraDrops.add(drop.clone());
        }

        drops.addAll(extraDrops);
    }
}
