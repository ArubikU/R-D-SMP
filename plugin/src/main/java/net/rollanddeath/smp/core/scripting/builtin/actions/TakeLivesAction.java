package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Optional;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.Bukkit;
import net.rollanddeath.smp.integration.PlaceholderUtil;
import org.bukkit.entity.Player;

final class TakeLivesAction {
    private TakeLivesAction() {
    }

    static void register() {
        ActionRegistrar.register("take_lives", TakeLivesAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object amountSpec = Resolvers.plain(raw, "amount");
        if (amountSpec == null) return null;
        Object msgSpec = Resolvers.plain(raw, "message");
        Object colorSpec = Resolvers.plain(raw, "color");
        return create(amountSpec, msgSpec, colorSpec);
    }

    private static Action create(Object amountSpec, Object messageSpec, Object colorSpec) {
        return ctx -> execute(ctx, amountSpec, messageSpec, colorSpec);
    }

    private static ActionResult execute(ScriptContext ctx, Object amountSpec, Object messageSpec, Object colorSpec) {
        Player player = ctx.player();
        RollAndDeathSMP plugin = ctx.plugin();

        if (plugin == null || player == null || !plugin.getLifeManager().isEnabled()) {
            return ActionResult.ALLOW;
        }

        Integer resolvedAmount = Resolvers.integer(ctx, amountSpec);
        int amount = resolvedAmount != null ? resolvedAmount : 0;
        if (amount <= 0) return ActionResult.ALLOW;

        Bukkit.getScheduler().runTask(plugin, () -> {
            for (int i = 0; i < amount; i++) {
                plugin.getLifeManager().removeLife(player);
            }
        });

        String message = Resolvers.string(ctx, messageSpec);
        if (message != null && !message.isBlank()) {
            NamedTextColor c = Optional.ofNullable(Resolvers.color(ctx, colorSpec)).orElse(NamedTextColor.RED);
            String text = PlaceholderUtil.resolvePlaceholders(plugin, player, message);
            player.sendMessage(Component.text(text, c));
        }

        return ActionResult.ALLOW;
    }
}
