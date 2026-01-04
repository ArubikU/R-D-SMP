package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

final class DuplicateDropsAction {
    private DuplicateDropsAction() {}

    static void register() {
        ActionRegistrar.register("duplicate_drops", DuplicateDropsAction::parse, "duplicate_entity_death_drops");
    }

    private static Action parse(Map<?, ?> raw) {
        Integer multiplier = Resolvers.integer(null, raw, "multiplier", "amount");
        int mult = multiplier != null ? multiplier : 2;

        return ctx -> {
            if (ctx.event() instanceof EntityDeathEvent deathEvent) {
                List<ItemStack> drops = deathEvent.getDrops();
                List<ItemStack> toAdd = new ArrayList<>();
                
                for (ItemStack is : drops) {
                    if (is != null) {
                        ItemStack copy = is.clone();
                        copy.setAmount(is.getAmount() * (mult - 1)); // Add (mult-1) times more
                        toAdd.add(copy);
                    }
                }
                
                drops.addAll(toAdd);
            }
            return ActionResult.ALLOW;
        };
    }
}
