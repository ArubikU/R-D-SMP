package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.concurrent.ThreadLocalRandom;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

/** Spawns a custom mob (Includer) at a stored location key, optionally within a radius and stores the first spawned. */
public final class SpawnMobAtKeyAction {
    private SpawnMobAtKeyAction() {
    }

    static void register() {
        ActionRegistrar.register(
            "spawn_mob_at_key",
            SpawnMobAtKeyAction::parse,
            "spawn_custom_mob_at_key",
            "spawn_mob_type_at_key"
        );
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object where = firstNonNull(raw, "where", "location", "key", "location_key");
        String mobType = Resolvers.string(null, raw, "mob_type", "type", "mob");
        if (where == null || mobType == null || mobType.isBlank()) return null;
        Integer count = Resolvers.integer(null, raw, "count");
        Integer radius = Resolvers.integer(null, raw, "radius");
        Integer yOffset = Resolvers.integer(null, raw, "y_offset");
        String storeKey = Resolvers.string(null, raw, "store_key", "store_as", "entity_store", "out");

        int c = count != null ? Math.max(1, count) : 1;
        int r = radius != null ? Math.max(0, radius) : 0;
        int yo = yOffset != null ? yOffset : 0;

        return ctx -> execute(ctx, where, mobType, c, r, yo, storeKey);
    }

    private static ActionResult execute(ScriptContext ctx, Object locationSpec, String mobType, int count, int radius, int yOffset, String storeKey) {
        RollAndDeathSMP plugin = ctx.plugin();
        if (plugin == null || plugin.getMobManager() == null) return ActionResult.ALLOW;

        Location base = Resolvers.location(locationSpec, ctx, ctx.player() != null ? ctx.player().getWorld() : null);
        if (base == null) return ActionResult.ALLOW;
        World world = base.getWorld();
        if (world == null) return ActionResult.ALLOW;

        String resolved = null;
        String rawType = mobType.trim();
        for (String id : plugin.getMobManager().getMobIds()) {
            if (id.equalsIgnoreCase(rawType)) {
                resolved = id;
                break;
            }
        }
        if (resolved == null) return ActionResult.ALLOW;

        String finalType = resolved;
        java.util.concurrent.atomic.AtomicReference<LivingEntity> firstSpawned = new java.util.concurrent.atomic.AtomicReference<>();

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            for (int i = 0; i < Math.max(1, count); i++) {
                Location at = base.clone().add(0, yOffset, 0);
                if (radius > 0) {
                    double dx = (ThreadLocalRandom.current().nextDouble() * 2 - 1) * radius;
                    double dz = (ThreadLocalRandom.current().nextDouble() * 2 - 1) * radius;
                    at.add(dx, 0, dz);
                }
                LivingEntity spawned;
                try {
                    spawned = plugin.getMobManager().spawnMob(finalType, at);
                } catch (Exception ignored) {
                    continue;
                }
                if (spawned != null && firstSpawned.get() == null) {
                    firstSpawned.set(spawned);
                }
            }
        });

        if (storeKey != null && !storeKey.isBlank()) {
            LivingEntity spawned = firstSpawned.get();
            if (spawned != null) {
                ctx.setGenericVarCompat(storeKey.trim(), spawned);
            }
        }

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
