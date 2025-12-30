package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

final class GiveItemAction {
    private GiveItemAction() {}

    static void register() {
        ActionRegistrar.register("give_item", GiveItemAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String materialName = Resolvers.string(null, raw, "material");
        if (materialName == null || materialName.isBlank()) return null;
        Integer amount = Resolvers.integer(null, raw, "amount");
        int a = amount != null ? Math.max(1, amount) : 1;

        return ctx -> {
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            
            Material mat = Material.getMaterial(materialName.toUpperCase());
            if (mat == null) return ActionResult.ALLOW;
            
            ActionUtils.runSync(ctx.plugin(), () -> player.getInventory().addItem(new ItemStack(mat, a)));
            return ActionResult.ALLOW;
        };
    }
}
