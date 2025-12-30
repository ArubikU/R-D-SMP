package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Optional;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.builtin.BuiltInActions;
import net.rollanddeath.smp.integration.PlaceholderUtil;
import org.bukkit.entity.Player;

final class BroadcastAction {
    private BroadcastAction() {
    }

    static void register() {
        ActionRegistrar.register("broadcast", BroadcastAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object msgSpec = Resolvers.plain(raw, "message");
        Object msgKeySpec = Resolvers.plain(raw, "message_key", "key");
        Object colorSpec = Resolvers.plain(raw, "color");
        if (msgSpec == null && msgKeySpec == null) return null;
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null) return ActionResult.ALLOW;

            String msg = Resolvers.string(ctx, msgSpec);
            if ((msg == null || msg.isBlank()) && msgKeySpec != null) {
                String key = Resolvers.string(ctx, msgKeySpec);
                if (key != null && !key.isBlank()) {
                    Object v;
                    try {
                        v = ctx.getValue(key);
                    } catch (Exception ignored) {
                        v = null;
                    }
                    if (v != null) {
                        msg = String.valueOf(v);
                    }
                }
            }

            if (msg == null || msg.isBlank()) return ActionResult.ALLOW;

            NamedTextColor color = Optional.ofNullable(Resolvers.color(ctx, colorSpec)).orElse(NamedTextColor.YELLOW);
            String text = PlaceholderUtil.resolvePlaceholders(plugin, player, msg);
            if (player != null) {
                text = text.replace("%player%", player.getName());
            }
            String textFinal = text;
            NamedTextColor colorFinal = color;
            BuiltInActions.runSync(plugin, () -> plugin.getServer().broadcast(Component.text(textFinal, colorFinal)));
            return ActionResult.ALLOW;
        };
    }
}