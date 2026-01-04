package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Optional;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.integration.PlaceholderUtil;
import org.bukkit.entity.Player;

final class SetLivesAction {
    private SetLivesAction() {
    }

    static void register() {
        ActionRegistrar.register("set_lives", SetLivesAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object valueSpec = Resolvers.plain(raw, "value");
        if (valueSpec == null) return null;
        Object msgSpec = Resolvers.plain(raw, "message");
        Object colorSpec = Resolvers.plain(raw, "color");
        return create(valueSpec, msgSpec, colorSpec);
    }

    private static Action create(Object valueSpec, Object messageSpec, Object colorSpec) {
        return ctx -> execute(ctx, valueSpec, messageSpec, colorSpec);
    }

    private static ActionResult execute(ScriptContext ctx, Object valueSpec, Object messageSpec, Object colorSpec) {
        Player player = ctx.player();
        RollAndDeathSMP plugin = ctx.plugin();

        if (plugin == null || player == null || !plugin.getLifeManager().isEnabled()) {
            return ActionResult.ALLOW;
        }

        Integer resolvedValue = Resolvers.integer(ctx, valueSpec);
        if (resolvedValue == null) return ActionResult.ALLOW;

        ActionUtils.runSync(plugin, () -> plugin.getLifeManager().setLives(player, resolvedValue));

        String message = Resolvers.string(ctx, messageSpec);
        if (message != null && !message.isBlank()) {
            NamedTextColor c = Optional.ofNullable(Resolvers.color(ctx, colorSpec)).orElse(NamedTextColor.YELLOW);
            String text = PlaceholderUtil.resolvePlaceholders(plugin, player, message);
            player.sendMessage(Component.text(text, c));
        }

        return ActionResult.ALLOW;
    }
}
