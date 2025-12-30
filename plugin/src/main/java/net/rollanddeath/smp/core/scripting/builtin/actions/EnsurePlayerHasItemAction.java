package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

final class EnsurePlayerHasItemAction {
    private EnsurePlayerHasItemAction() {}

    static void register() {
        ActionRegistrar.register("ensure_player_has_item", EnsurePlayerHasItemAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String materialName = Resolvers.string(null, raw, "material");
        if (materialName == null || materialName.isBlank()) return null;
        Integer amount = Resolvers.integer(null, raw, "amount");
        int a = amount != null ? Math.max(1, amount) : 1;
        
        Material mat = Material.getMaterial(materialName.toUpperCase());
        if (mat == null) return null;

        final Material matFinal = mat;

        return ctx -> {
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            
            ActionUtils.runSync(ctx.plugin(), () -> {
                if (player.getInventory().contains(matFinal)) return;
                player.getInventory().addItem(new ItemStack(matFinal, a));
            });
            return ActionResult.ALLOW;
        };
    }
}
