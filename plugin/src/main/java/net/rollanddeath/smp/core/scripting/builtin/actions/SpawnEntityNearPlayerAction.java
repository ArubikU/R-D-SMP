package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/** Spawns a vanilla entity near the invoking player with optional per-chunk cap. */
public final class SpawnEntityNearPlayerAction {
    private SpawnEntityNearPlayerAction() {
    }

    static void register() {
        ActionRegistrar.register("spawn_entity_near_player", SpawnEntityNearPlayerAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String entityType = Resolvers.string(null, raw, "entity_type", "entity", "type");
        if (entityType == null || entityType.isBlank()) return null;

        String capEntityType = Resolvers.string(null, raw, "cap_entity_type", "cap_entity", "cap_type");
        Integer yOffset = Resolvers.integer(null, raw, "y_offset", "y");
        Integer radius = Resolvers.integer(null, raw, "radius", "r");
        Integer maxY = Resolvers.integer(null, raw, "max_y");
        Integer maxPerChunk = Resolvers.integer(null, raw, "max_per_chunk", "per_chunk_cap", "chunk_cap");
        boolean requireStorm = raw.get("require_storm") instanceof Boolean b ? b : false;

        int yo = yOffset != null ? yOffset : 10;
        int r = radius != null ? Math.max(0, radius) : 5;
        int my = maxY != null ? maxY : 320;
        int cap = maxPerChunk != null ? Math.max(0, maxPerChunk) : 0;
        String capType = (capEntityType != null && !capEntityType.isBlank()) ? capEntityType : entityType;

        return ctx -> execute(ctx, entityType, capType, yo, r, my, cap, requireStorm);
    }

    private static ActionResult execute(ScriptContext ctx, String entityType, String capEntityType, int yOffset, int radius, int maxY, int maxPerChunk, boolean requireStorm) {
        RollAndDeathSMP plugin = ctx.plugin();
        Player player = ctx.player();
        if (plugin == null || player == null || player.getWorld() == null) return ActionResult.ALLOW;
        if (requireStorm && !player.getWorld().hasStorm()) return ActionResult.ALLOW;

        EntityType spawnType = Resolvers.resolveEntityType(entityType);
        if (spawnType == null) return ActionResult.ALLOW;
        EntityType capType = Optional.ofNullable(Resolvers.resolveEntityType(capEntityType)).orElse(spawnType);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int dx = radius > 0 ? random.nextInt(-radius, radius + 1) : 0;
        int dz = radius > 0 ? random.nextInt(-radius, radius + 1) : 0;

        Location loc = player.getLocation().clone().add(dx, yOffset, dz);
        if (loc.getY() >= maxY || loc.getWorld() == null) return ActionResult.ALLOW;

        if (maxPerChunk > 0) {
            Chunk chunk = loc.getChunk();
            try {
                long count = Arrays.stream(chunk.getEntities()).filter(e -> e != null && e.getType() == capType).count();
                if (count >= maxPerChunk) return ActionResult.ALLOW;
            } catch (Exception ignored) {
            }
        }

        Bukkit.getScheduler().runTask(plugin, () -> loc.getWorld().spawnEntity(loc, spawnType));
        return ActionResult.ALLOW;
    }
}
