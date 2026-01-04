package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.core.scripting.ActionResult;
import org.bukkit.entity.Player;

final class SlipDropHandItemAction {
    private SlipDropHandItemAction() {
    }

    static void register() {
        ActionRegistrar.register("slip_drop_hand_item", SlipDropHandItemAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String msg = Resolvers.string(null, raw, "message");
        String color = Resolvers.string(null, raw, "color");
        
        return ctx -> {
            Player p = ctx.player();
            if (p != null) {
                p.dropItem(true);
                if (msg != null) {
                    NamedTextColor c = Resolvers.resolveColor(color);
                    if (c == null) c = NamedTextColor.RED;
                    p.sendMessage(Component.text(msg, c));
                }
            }
            return ActionResult.ALLOW;
        };
    }
}
