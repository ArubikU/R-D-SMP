package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

final class RotateOnlinePlayerPositionsAction {
    private RotateOnlinePlayerPositionsAction() {}

    static void register() {
        ActionRegistrar.register("rotate_online_player_positions", RotateOnlinePlayerPositionsAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String nextKey = Resolvers.string(null, raw, "next_key", "key");
        if (nextKey == null || nextKey.isBlank()) return null;
        Long intervalMs = Resolvers.longVal(null, raw, "interval_ms", "interval", "ms");
        long interval = intervalMs != null ? Math.max(0L, intervalMs) : 1_800_000L;
        String message = Resolvers.string(null, raw, "message");
        String color = Resolvers.string(null, raw, "color");
        boolean shuffle = raw.get("shuffle") instanceof Boolean b ? b : true;

        return ctx -> {
            long now = System.currentTimeMillis();
            long next = -1L;
            try {
                Object v = ctx.getValue(nextKey);
                if (v instanceof Number n) next = n.longValue();
                else if (v instanceof String s) next = Long.parseLong(s.trim());
            } catch (Exception ignored) {
                next = -1L;
            }

            if (next > 0L && now < next) return ActionResult.ALLOW;

            ctx.setGenericVarCompat(nextKey, now + Math.max(0L, interval));

            final NamedTextColor cFinal = java.util.Optional.ofNullable(Resolvers.resolveColor(color)).orElse(NamedTextColor.YELLOW);
            
            ActionUtils.runSync(ctx.plugin(), () -> {
                List<Player> players = new java.util.ArrayList<>(Bukkit.getOnlinePlayers());
                if (players.size() < 2) return;

                if (shuffle) {
                    java.util.Collections.shuffle(players);
                }

                List<Location> locations = new java.util.ArrayList<>(players.size());
                for (Player p : players) {
                    try {
                        locations.add(p.getLocation());
                    } catch (Exception ignored) {
                        locations.add(null);
                    }
                }

                for (int i = 0; i < players.size(); i++) {
                    Player p = players.get(i);
                    Location loc = locations.get((i + 1) % players.size());
                    if (p == null || loc == null) continue;
                    try {
                        p.teleport(loc);
                    } catch (Exception ignored) {
                    }
                    if (message != null && !message.isBlank()) {
                        try {
                            p.sendMessage(Component.text(message, cFinal));
                        } catch (Exception ignored) {
                        }
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }
}
