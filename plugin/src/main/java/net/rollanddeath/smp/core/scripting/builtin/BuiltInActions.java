package net.rollanddeath.smp.core.scripting.builtin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.integration.PlaceholderUtil;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class BuiltInActions {

    private BuiltInActions() {
    }

    public static Action parse(Map<?, ?> raw) {
        String type = getString(raw, "type");
        if (type == null) return null;
        type = type.trim().toLowerCase(Locale.ROOT);

        Action base = switch (type) {
            case "message" -> {
                String msg = getString(raw, "message");
                if (msg == null) yield null;
                String color = getString(raw, "color");
                yield message(msg, color);
            }
            case "deny" -> {
                String msg = getString(raw, "message");
                String color = getString(raw, "color");
                yield deny(msg, color);
            }
            case "command" -> {
                String cmd = getString(raw, "command");
                if (cmd == null || cmd.isBlank()) yield null;
                String as = Optional.ofNullable(getString(raw, "as")).orElse("console");
                yield command(cmd, as);
            }
            case "take_lives" -> {
                Integer amt = getInt(raw, "amount");
                if (amt == null || amt <= 0) yield null;
                String msg = getString(raw, "message");
                String color = getString(raw, "color");
                yield takeLives(amt, msg, color);
            }
            case "add_lives" -> {
                Integer amt = getInt(raw, "amount");
                if (amt == null || amt <= 0) yield null;
                String msg = getString(raw, "message");
                String color = getString(raw, "color");
                yield addLives(amt, msg, color);
            }
            case "set_lives" -> {
                Integer value = getInt(raw, "value");
                if (value == null) yield null;
                String msg = getString(raw, "message");
                String color = getString(raw, "color");
                yield setLives(value, msg, color);
            }
            default -> null;
        };

        if (base == null) return null;

        // Permite condicionar acciones individuales desde YAML:
        // - { type: command, command: "...", condition: { type: role_is, value: ENGINEER } }
        // - { type: message, message: "...", when: { type: min_day, value: 10 } }
        Object condObj = raw.get("condition");
        if (!(condObj instanceof Map<?, ?>)) {
            condObj = raw.get("when");
        }
        if (condObj instanceof Map<?, ?> condMap) {
            Condition cond = BuiltInConditions.parse(condMap);
            if (cond == null) return null;
            return ctx -> cond.test(ctx) ? base.run(ctx) : ActionResult.ALLOW;
        }

        return base;
    }

    public static Action message(String message, String color) {
        return ctx -> {
            Player player = ctx.player();
            RollAndDeathSMP plugin = ctx.plugin();
            NamedTextColor c = parseColor(color).orElse(NamedTextColor.YELLOW);
            String text = PlaceholderUtil.resolvePlaceholders(plugin, player, message);
            player.sendMessage(Component.text(text, c));
            return ActionResult.ALLOW;
        };
    }

    public static Action deny(String message, String color) {
        return ctx -> {
            Player player = ctx.player();
            RollAndDeathSMP plugin = ctx.plugin();
            if (message != null && !message.isBlank()) {
                NamedTextColor c = parseColor(color).orElse(NamedTextColor.RED);
                String text = PlaceholderUtil.resolvePlaceholders(plugin, player, message);
                player.sendMessage(Component.text(text, c));
            }
            return ActionResult.DENY;
        };
    }

    public static Action command(String command, String as) {
        return ctx -> {
            Player player = ctx.player();
            RollAndDeathSMP plugin = ctx.plugin();

            String cmd = PlaceholderUtil.resolvePlaceholders(plugin, player, command);
            cmd = cmd.replace("%player%", player.getName());
            cmd = cmd.startsWith("/") ? cmd.substring(1) : cmd;

            String mode = as == null ? "console" : as.trim().toLowerCase(Locale.ROOT);
            if ("player".equals(mode)) {
                player.performCommand(cmd);
            } else {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
            }

            return ActionResult.ALLOW;
        };
    }

    public static Action takeLives(int amount, String message, String color) {
        return ctx -> {
            Player player = ctx.player();
            RollAndDeathSMP plugin = ctx.plugin();

            for (int i = 0; i < amount; i++) {
                plugin.getLifeManager().removeLife(player);
            }

            if (message != null && !message.isBlank()) {
                NamedTextColor c = parseColor(color).orElse(NamedTextColor.RED);
                String text = PlaceholderUtil.resolvePlaceholders(plugin, player, message);
                player.sendMessage(Component.text(text, c));
            }

            return ActionResult.ALLOW;
        };
    }

    public static Action addLives(int amount, String message, String color) {
        return ctx -> {
            Player player = ctx.player();
            RollAndDeathSMP plugin = ctx.plugin();

            for (int i = 0; i < amount; i++) {
                plugin.getLifeManager().addLife(player);
            }

            if (message != null && !message.isBlank()) {
                NamedTextColor c = parseColor(color).orElse(NamedTextColor.GREEN);
                String text = PlaceholderUtil.resolvePlaceholders(plugin, player, message);
                player.sendMessage(Component.text(text, c));
            }

            return ActionResult.ALLOW;
        };
    }

    public static Action setLives(int value, String message, String color) {
        return ctx -> {
            Player player = ctx.player();
            RollAndDeathSMP plugin = ctx.plugin();

            plugin.getLifeManager().setLives(player, value);

            if (message != null && !message.isBlank()) {
                NamedTextColor c = parseColor(color).orElse(NamedTextColor.YELLOW);
                String text = PlaceholderUtil.resolvePlaceholders(plugin, player, message);
                player.sendMessage(Component.text(text, c));
            }

            return ActionResult.ALLOW;
        };
    }

    private static Optional<NamedTextColor> parseColor(String color) {
        if (color == null || color.isBlank()) return Optional.empty();
        try {
            return Optional.of(NamedTextColor.NAMES.value(color.trim().toLowerCase(Locale.ROOT)));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private static String getString(Map<?, ?> raw, String key) {
        Object v = raw.get(key);
        return (v instanceof String s) ? s : null;
    }

    private static Integer getInt(Map<?, ?> raw, String key) {
        Object v = raw.get(key);
        if (v instanceof Number n) return n.intValue();
        if (v instanceof String s) {
            try {
                return Integer.parseInt(s.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
