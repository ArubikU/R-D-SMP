package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DemeterBlessingModifier extends Modifier {

    public DemeterBlessingModifier(JavaPlugin plugin) {
        super(plugin, "Bendición de Demeter", ModifierType.BLESSING, "Cultivos crecen instantáneamente con click derecho.");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
            Block block = event.getClickedBlock();
            BlockData data = block.getBlockData();

            if (data instanceof Ageable ageable) {
                if (ageable.getAge() < ageable.getMaximumAge()) {
                    ageable.setAge(ageable.getMaximumAge());
                    block.setBlockData(ageable);
                    block.getWorld().playEffect(block.getLocation(), org.bukkit.Effect.VILLAGER_PLANT_GROW, 0);
                }
            }
        }
    }
}
