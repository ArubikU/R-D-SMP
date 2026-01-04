package net.rollanddeath.smp.integration;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.RoleType;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.Locale;

public final class PlaceholderUtil {

    private PlaceholderUtil() {
    }

    /**
     * Resuelve placeholders.
     * - Si PlaceholderAPI está instalado: usa PlaceholderAPI.
     * - Si no: soporta un subset útil (%player%, %rdsmp_lives%, %rdsmp_day%, %rdsmp_role%).
     */
    public static String resolvePlaceholders(RollAndDeathSMP plugin, Player player, String text) {
        if (text == null) return null;

        String out = text;
        if (player != null) {
            out = out.replace("%player%", player.getName());
        }

        // Fallback interno (cuando no hay PlaceholderAPI)
        out = replaceInternal(plugin, player, out);

        // PlaceholderAPI (si está presente)
        try {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                out = PlaceholderAPI.setPlaceholders(player, out);
            }
        } catch (NoClassDefFoundError ignored) {
            // PlaceholderAPI no está realmente disponible en runtime
        } catch (Throwable ignored) {
            // no-op: nunca queremos romper por placeholders
        }

        return out;
    }

    private static String replaceInternal(RollAndDeathSMP plugin, Player player, String text) {
        String out = text;

        if (out.contains("%rdsmp_lives%") && plugin.getLifeManager() != null) {
            if (!plugin.getLifeManager().isEnabled() || player == null) {
                out = out.replace("%rdsmp_lives%", "off");
            } else {
                out = out.replace("%rdsmp_lives%", String.valueOf(plugin.getLifeManager().getLives(player)));
            }
        }

        if (out.contains("%rdsmp_day%") && plugin.getGameManager() != null) {
            out = out.replace("%rdsmp_day%", String.valueOf(plugin.getGameManager().getCurrentDay()));
        }

        if (out.contains("%rdsmp_role%") && plugin.getRoleManager() != null && player != null) {
            RoleType role = plugin.getRoleManager().getPlayerRole(player);
            out = out.replace("%rdsmp_role%", role != null ? role.name().toLowerCase(Locale.ROOT) : "none");
        }

        return out;
    }
}
