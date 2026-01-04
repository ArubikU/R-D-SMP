package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

final class RestoreAnvilAction {
    private RestoreAnvilAction() {}

    static void register() {
        ActionRegistrar.register("restore_anvil", RestoreAnvilAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        return ctx -> {
            if (ctx.event() instanceof InventoryCloseEvent e) {
                Inventory inv = e.getInventory();
                Location loc = inv.getLocation();
                
                if (loc != null) {
                    ActionUtils.runSync(ctx.plugin(), () -> {
                        Block b = loc.getBlock();
                        Material type = b.getType();
                        if (type == Material.CHIPPED_ANVIL || type == Material.DAMAGED_ANVIL) {
                            // Preserve rotation
                            if (b.getBlockData() instanceof Directional d) {
                                b.setType(Material.ANVIL);
                                if (b.getBlockData() instanceof Directional newD) {
                                    newD.setFacing(d.getFacing());
                                    b.setBlockData(newD);
                                }
                            } else {
                                b.setType(Material.ANVIL);
                            }
                        }
                    });
                }
            }
            return ActionResult.ALLOW;
        };
    }
}
