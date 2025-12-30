package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

final class SetPlayerInventoryHideTooltipAction {
    private SetPlayerInventoryHideTooltipAction() {}

    static void register() {
        ActionRegistrar.register("set_player_inventory_hide_tooltip", SetPlayerInventoryHideTooltipAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Boolean hide = raw.get("hide") instanceof Boolean b ? b : (raw.get("value") instanceof Boolean bb ? bb : null);
        if (hide == null) return null;

        return ctx -> {
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            
            ActionUtils.runSync(ctx.plugin(), () -> {
                for (ItemStack item : player.getInventory().getContents()) {
                    applyHideTooltip(item, hide);
                }
            });
            return ActionResult.ALLOW;
        };
    }

    private static void applyHideTooltip(ItemStack item, boolean hide) {
        if (item == null || item.getType().isAir()) return;
        try {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;
            meta.setHideTooltip(hide);
            item.setItemMeta(meta);
        } catch (NoSuchMethodError ignored) {
        } catch (Exception ignored) {
        }
    }
}
