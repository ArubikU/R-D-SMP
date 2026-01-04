package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.integration.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/** Broadcasts a message resolving placeholders and location tokens. */
public final class BroadcastWithLocationAction {
    private BroadcastWithLocationAction() {
    }

    static void register() {
        ActionRegistrar.register("broadcast_with_location", BroadcastWithLocationAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String msg = Resolvers.string(null, raw, "message", "msg");
        Object loc = firstNonNull(raw, "location", "where", "location_key", "key");
        if (msg == null || msg.isBlank() || loc == null) return null;
        return ctx -> execute(ctx, loc, msg);
    }

    private static ActionResult execute(ScriptContext ctx, Object locationSpec, String message) {
        RollAndDeathSMP plugin = ctx.plugin();
        Player player = ctx.player();
        if (plugin == null) return ActionResult.ALLOW;

        Location loc = Resolvers.location(ctx, locationSpec, player != null ? player.getWorld() : null);
        if (loc == null) return ActionResult.ALLOW;

        String msg = PlaceholderUtil.resolvePlaceholders(plugin, player, message);
        msg = replaceLocationTokens(msg, loc);

        String msgFinal = msg;
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            try {
                Bukkit.broadcast(MiniMessage.miniMessage().deserialize(msgFinal));
            } catch (Exception ignored) {
                Bukkit.broadcast(Component.text(msgFinal));
            }
        });
        return ActionResult.ALLOW;
    }

    private static String replaceLocationTokens(String message, Location loc) {
        if (message == null || loc == null) return message;
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        String world = loc.getWorld() != null ? loc.getWorld().getName() : "";

        return message
            // Formato {x} (usado en YAML)
            .replace("{x}", String.valueOf(x))
            .replace("{y}", String.valueOf(y))
            .replace("{z}", String.valueOf(z))
            .replace("{world}", world)
            .replace("{world_name}", world)
            // Formato %x% (alternativo)
            .replace("%x%", String.valueOf(x))
            .replace("%y%", String.valueOf(y))
            .replace("%z%", String.valueOf(z))
            .replace("%world%", world)
            .replace("%world_name%", world);
    }

    private static Object firstNonNull(java.util.Map<?, ?> raw, String... keys) {
        for (String k : keys) {
            if (raw.containsKey(k) && raw.get(k) != null) {
                return raw.get(k);
            }
        }
        return null;
    }
}
