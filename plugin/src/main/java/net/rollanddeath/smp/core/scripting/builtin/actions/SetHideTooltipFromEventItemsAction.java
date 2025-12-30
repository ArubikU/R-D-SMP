package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

final class SetHideTooltipFromEventItemsAction {
    private SetHideTooltipFromEventItemsAction() {}

    static void register() {
        ActionRegistrar.register("set_hide_tooltip_from_event_items", SetHideTooltipFromEventItemsAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Boolean hide = raw.get("hide") instanceof Boolean b ? b : (raw.get("value") instanceof Boolean bb ? bb : null);
        if (hide == null) return null;

        return ctx -> {
            Object ev = ctx.nativeEvent();
            ActionUtils.runSync(ctx.plugin(), () -> {
                if (ev instanceof InventoryClickEvent ice) {
                    applyHideTooltip(ice.getCurrentItem(), hide);
                    applyHideTooltip(ice.getCursor(), hide);
                }
                if (ev instanceof EntityPickupItemEvent epi) {
                    try {
                        if (epi.getItem() != null) {
                            applyHideTooltip(epi.getItem().getItemStack(), hide);
                        }
                    } catch (Exception ignored) {
                    }
                }
                if (ev instanceof PrepareItemCraftEvent pic) {
                    try {
                        applyHideTooltip(pic.getInventory().getResult(), hide);
                    } catch (Exception ignored) {
                    }
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
