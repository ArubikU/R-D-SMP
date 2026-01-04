package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Locale;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/** Places torches around a center location at the given distance. */
public final class PlaceTorchesAroundAction {
    private PlaceTorchesAroundAction() {
    }

    static void register() {
        ActionRegistrar.register("place_torches_around", PlaceTorchesAroundAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object center = firstNonNull(raw, "center", "where", "location", "center_key", "key");
        if (center == null) return null;
        Integer distanceRaw = Resolvers.integer(null, raw, "distance");
        if (distanceRaw == null) distanceRaw = Resolvers.integer(null, raw, "radius");
        final int distance = distanceRaw != null ? Math.max(1, distanceRaw) : 1;
        final boolean includeDiagonals = raw.get("include_diagonals") instanceof Boolean b ? b : false;
        final String material = Resolvers.string(null, raw, "material");
        return ctx -> execute(ctx, center, distance, includeDiagonals, material);
    }

    private static ActionResult execute(ScriptContext ctx, Object centerSpec, int distance, boolean includeDiagonals, String materialRaw) {
        var plugin = ctx.plugin();
        if (plugin == null) return ActionResult.ALLOW;

        Material mat;
        try {
            mat = Material.valueOf(materialRaw != null ? materialRaw.trim().toUpperCase(Locale.ROOT) : "TORCH");
        } catch (Exception ignored) {
            mat = Material.TORCH;
        }

        Location center = Resolvers.location(ctx, centerSpec, ctx.player() != null ? ctx.player().getWorld() : null);
        if (center == null || center.getWorld() == null) return ActionResult.ALLOW;

        int d = Math.max(1, distance);
        Material matFinal = mat;

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            int[][] offsets = includeDiagonals
                ? new int[][] { { d, 0 }, { -d, 0 }, { 0, d }, { 0, -d }, { d, d }, { d, -d }, { -d, d }, { -d, -d } }
                : new int[][] { { d, 0 }, { -d, 0 }, { 0, d }, { 0, -d } };

            for (int[] off : offsets) {
                Location torchLoc = center.clone().add(off[0], 0, off[1]);
                Block below = torchLoc.getBlock().getRelative(0, -1, 0);
                if (!below.getType().isSolid()) continue;
                Block at = torchLoc.getBlock();
                if (!at.getType().isAir()) continue;
                try {
                    at.setType(matFinal, true);
                } catch (Exception ignored) {
                }
            }
        });

        return ActionResult.ALLOW;
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
