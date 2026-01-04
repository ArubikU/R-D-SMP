package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

final class FortuneTouchBestDropAction {
    private FortuneTouchBestDropAction() {}

    static void register() {
        ActionRegistrar.register("fortune_touch_best_drop", FortuneTouchBestDropAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Integer attempts = Resolvers.integer(null, raw, "attempts");
        Integer level = Resolvers.integer(null, raw, "fortune_level");
        
        int tries = attempts != null ? attempts : 10;
        int fortune = level != null ? level : 3;

        return ctx -> {
            if (ctx.event() instanceof BlockBreakEvent e) {
                Block b = e.getBlock();
                ItemStack tool = new ItemStack(Material.DIAMOND_PICKAXE);
                tool.addUnsafeEnchantment(Enchantment.FORTUNE, fortune);
                
                Collection<ItemStack> bestDrops = new ArrayList<>();
                int maxCount = -1;
                
                for (int i = 0; i < tries; i++) {
                    Collection<ItemStack> drops = b.getDrops(tool);
                    int count = drops.stream().mapToInt(ItemStack::getAmount).sum();
                    if (count > maxCount) {
                        maxCount = count;
                        bestDrops = drops;
                    }
                }
                
                e.setDropItems(false);
                final Collection<ItemStack> finalDrops = bestDrops;
                
                ActionUtils.runSync(ctx.plugin(), () -> {
                    for (ItemStack is : finalDrops) {
                        b.getWorld().dropItemNaturally(b.getLocation(), is);
                    }
                });
            }
            return ActionResult.ALLOW;
        };
    }
}
