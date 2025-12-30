package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/** Finds a valid top block near a center point and stores its location. */
public final class FindTopBlockNearAction {
    private FindTopBlockNearAction() {
    }

    static void register() {
        ActionRegistrar.register("find_top_block_near", FindTopBlockNearAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String storeKey = Resolvers.string(null, raw, "store_key", "to", "key_out", "out");
        if (storeKey == null || storeKey.isBlank()) return null;

        String world = Optional.ofNullable(Resolvers.string(null, raw, "world")).orElse("world");
        Object center = firstNonNull(raw, "center", "where", "location", "center_key");
        Integer radius = Resolvers.integer(null, raw, "radius");
        Integer attempts = Resolvers.integer(null, raw, "attempts");
        boolean requireSolid = raw.get("require_solid") instanceof Boolean b ? b : true;
        boolean requireAirAbove = raw.get("require_air_above") instanceof Boolean b ? b : true;
        boolean avoidWater = raw.get("avoid_water") instanceof Boolean b ? b : true;

        int r = radius != null ? Math.max(1, radius) : 150;
        int tries = attempts != null ? Math.max(1, attempts) : 30;

        return ctx -> execute(ctx, world, center, r, tries, requireSolid, requireAirAbove, avoidWater, storeKey.trim());
    }

    private static ActionResult execute(
        ScriptContext ctx,
        String worldName,
        Object centerSpec,
        int radius,
        int attempts,
        boolean requireSolid,
        boolean requireAirAbove,
        boolean avoidWater,
        String storeKey
    ) {
        RollAndDeathSMP plugin = ctx.plugin();
        if (plugin == null) return ActionResult.ALLOW;

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            World world = Bukkit.getWorld(worldName != null ? worldName : "world");
            if (world == null) return;

            Location center = Resolvers.location(centerSpec, ctx, world);
            if (center == null) center = world.getSpawnLocation();

            Location best = null;
            int r = Math.max(1, radius);
            int tries = Math.max(1, attempts);

            for (int i = 0; i < tries; i++) {
                double angle = ThreadLocalRandom.current().nextDouble() * Math.PI * 2.0;
                double dist = r * Math.sqrt(ThreadLocalRandom.current().nextDouble());
                int dx = (int) Math.round(Math.cos(angle) * dist);
                int dz = (int) Math.round(Math.sin(angle) * dist);

                int x = center.getBlockX() + dx;
                int z = center.getBlockZ() + dz;
                int y = world.getHighestBlockYAt(x, z);
                Block base = world.getBlockAt(x, y, z);

                if (avoidWater) {
                    Material t = base.getType();
                    if (t == Material.WATER || t == Material.LAVA) continue;
                }
                if (requireSolid && !base.getType().isSolid()) continue;

                if (requireAirAbove) {
                    Block above = base.getRelative(0, 1, 0);
                    if (!above.getType().isAir()) continue;
                }

                best = base.getLocation();
                break;
            }

            if (best != null) {
                try {
                    ctx.setGenericVarCompat(storeKey, best);
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
