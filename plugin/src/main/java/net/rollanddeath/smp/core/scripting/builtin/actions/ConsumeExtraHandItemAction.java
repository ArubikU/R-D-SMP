package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/** Consumes items from the hand of the player triggering a PlayerInteractEvent. */
public final class ConsumeExtraHandItemAction {
    private ConsumeExtraHandItemAction() {
    }

    static void register() {
        ActionRegistrar.register("consume_extra_hand_item", ConsumeExtraHandItemAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Integer amount = Resolvers.integer(null, raw, "amount");
        int amt = amount != null ? Math.max(1, amount) : 1;
        return ctx -> execute(ctx, amt);
    }

    private static ActionResult execute(ScriptContext ctx, int amount) {
        RollAndDeathSMP plugin = ctx.plugin();
        Player player = ctx.player();
        if (plugin == null || player == null) return ActionResult.ALLOW;

        PlayerInteractEvent event = ctx.nativeEvent(PlayerInteractEvent.class);
        if (event == null) return ActionResult.ALLOW;

        ItemStack hand = event.getItem();
        if (hand == null) return ActionResult.ALLOW;

        int amt = Math.max(1, amount);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (hand.getAmount() > amt) {
                hand.setAmount(hand.getAmount() - amt);
            } else {
                player.getInventory().removeItem(new ItemStack(hand.getType(), amt));
            }
        });

        return ActionResult.ALLOW;
    }
}
