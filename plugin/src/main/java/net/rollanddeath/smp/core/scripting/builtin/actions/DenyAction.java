package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Optional;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;
import net.rollanddeath.smp.integration.PlaceholderUtil;
import org.bukkit.entity.Player;

final class DenyAction {
    private DenyAction() {
    }

    static void register() {
        ActionRegistrar.register("deny", DenyAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object msgSpec = Resolvers.plain(raw, "message");
        Object colorSpec = Resolvers.plain(raw, "color");
        return create(msgSpec, colorSpec);
    }

    private static Action create(Object messageSpec, Object colorSpec) {
        return ctx -> deny(ctx, messageSpec, colorSpec);
    }

    private static ActionResult deny(ScriptContext ctx, Object messageSpec, Object colorSpec) {
        Player player = ctx.player();
        RollAndDeathSMP plugin = ctx.plugin();
        String message = Resolvers.string(ctx, messageSpec);
        if (message != null && !message.isBlank()) {
            NamedTextColor c = Optional.ofNullable(Resolvers.color(ctx, colorSpec)).orElse(NamedTextColor.RED);
            String text = PlaceholderUtil.resolvePlaceholders(plugin, player, message);
            BuiltInActions.runSync(plugin, () -> player.sendMessage(Component.text(text, c)));
        }
        return ActionResult.DENY;
    }
}
