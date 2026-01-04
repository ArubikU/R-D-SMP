package net.rollanddeath.smp.core.scripting.builtin.conditions;

import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class InventoryFullCondition implements Condition {

    private final boolean expected;
    private final Object targetSpec;

    public static void register() {
        ConditionRegistrar.register("inventory_full", InventoryFullCondition::new, "player_inventory_full");
    }

    public InventoryFullCondition(Map<?, ?> spec) {
        this.expected = Resolvers.bool(null, spec.get("value")) != Boolean.FALSE; // default true
        this.targetSpec = spec.get("target");
    }

    @Override
    public boolean test(ScriptContext ctx) {
        List<Entity> targets = Resolvers.entities(ctx, targetSpec);
        if (targets.isEmpty()) {
            if (targetSpec == null && ctx.player() != null) {
                targets = List.of(ctx.player());
            } else {
                return false;
            }
        }

        boolean allMatch = true;
        for (Entity e : targets) {
            if (!(e instanceof InventoryHolder holder)) {
                allMatch = false;
                break;
            }
            boolean full = true;
            try {
                for (ItemStack it : holder.getInventory().getStorageContents()) {
                    if (it == null || it.getType().isAir()) {
                        full = false;
                        break;
                    }
                }
            } catch (Exception ignored) {
                full = false;
            }
            if (full != expected) {
                allMatch = false;
                break;
            }
        }
        return allMatch;
    }
}
