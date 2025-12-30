package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

final class AddEventDeathDropAction {
    private AddEventDeathDropAction() {}

    static void register() {
        ActionRegistrar.register("add_event_death_drop", AddEventDeathDropAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String materialName = Resolvers.string(null, raw, "material");
        if (materialName == null || materialName.isBlank()) return null;
        Integer amount = Resolvers.integer(null, raw, "amount");
        int a = amount != null ? Math.max(1, amount) : 1;

        return ctx -> {
            EntityDeathEvent ede = ctx.nativeEvent(EntityDeathEvent.class);
            if (ede == null) return ActionResult.ALLOW;
            
            Material mat = Material.getMaterial(materialName.toUpperCase());
            if (mat == null) return ActionResult.ALLOW;
            
            ede.getDrops().add(new ItemStack(mat, a));
            return ActionResult.ALLOW;
        };
    }
}
