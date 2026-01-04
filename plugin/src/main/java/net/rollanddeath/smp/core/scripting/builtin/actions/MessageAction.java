package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.integration.PlaceholderUtil;
import org.bukkit.entity.Player;

final class MessageAction {
    private MessageAction() {}

    static void register() {
        ActionRegistrar.register("message", MessageAction::parse, "send_message", "msg");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object msgSpec = Resolvers.plain(raw, "message", "text");
        Object colorSpec = Resolvers.plain(raw, "color");
        
        if (msgSpec == null) return null;

        return ctx -> {
            Player p = ctx.player();
            if (p == null) return ActionResult.ALLOW;

            String msg = Resolvers.string(ctx, msgSpec);
            if (msg == null) return ActionResult.ALLOW;

            NamedTextColor color = Optional.ofNullable(Resolvers.color(ctx, colorSpec)).orElse(NamedTextColor.WHITE);
            String text = PlaceholderUtil.resolvePlaceholders(ctx.plugin(), p, msg);
            text = text.replace("%player%", p.getName());
            
            p.sendMessage(Component.text(text, color));
            return ActionResult.ALLOW;
        };
    }
}
