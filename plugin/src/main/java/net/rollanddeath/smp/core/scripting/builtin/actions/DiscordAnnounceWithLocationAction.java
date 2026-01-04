package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Locale;

import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.integration.PlaceholderUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/** Sends a Discord announcement embedding location tokens. */
public final class DiscordAnnounceWithLocationAction {
    private DiscordAnnounceWithLocationAction() {
    }

    static void register() {
        ActionRegistrar.register("discord_announce_with_location", DiscordAnnounceWithLocationAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String title = Resolvers.string(null, raw, "title", "name");
        String msg = Resolvers.string(null, raw, "message", "content", "msg");
        Object loc = firstNonNull(raw, "location", "where", "location_key", "key");
        String color = Resolvers.string(null, raw, "color");
        if (title == null || title.isBlank() || msg == null || msg.isBlank() || loc == null) return null;
        return ctx -> execute(ctx, loc, title, msg, color);
    }

    private static ActionResult execute(ScriptContext ctx, Object locationSpec, String title, String message, String colorRaw) {
        RollAndDeathSMP plugin = ctx.plugin();
        Player player = ctx.player();
        if (plugin == null) return ActionResult.ALLOW;

        Location loc = Resolvers.location(ctx, locationSpec, player != null ? player.getWorld() : null);
        if (loc == null) return ActionResult.ALLOW;

        String t = PlaceholderUtil.resolvePlaceholders(plugin, player, title);
        String msg = PlaceholderUtil.resolvePlaceholders(plugin, player, message);
        msg = replaceLocationTokens(msg, loc);

        NamedTextColor color;
        try {
            color = colorRaw != null ? NamedTextColor.NAMES.value(colorRaw.trim().toLowerCase(Locale.ROOT)) : NamedTextColor.GREEN;
        } catch (Exception ignored) {
            color = NamedTextColor.GREEN;
        }

        String titleFinal = t;
        String msgFinal = msg;
        NamedTextColor colorFinal = color;

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            try {
                var service = plugin.getDiscordService();
                if (service != null && service.isEnabled()) {
                    service.sendEventAnnouncement(titleFinal, msgFinal, colorFinal);
                }
            } catch (Exception ignored) {
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
