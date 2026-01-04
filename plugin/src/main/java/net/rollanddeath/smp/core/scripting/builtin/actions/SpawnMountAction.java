package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

final class SpawnMountAction {
    private SpawnMountAction() {}

    static void register() {
        ActionRegistrar.register("spawn_mount", SpawnMountAction::parse, "spawn_mount_for_mob");
    }

    private static Action parse(Map<?, ?> raw) {
        Object targetSpecRaw = raw.get("target"); // The passenger (mob)
        if (targetSpecRaw == null) targetSpecRaw = raw.get("passenger");
        if (targetSpecRaw == null) targetSpecRaw = raw.get("mob");
        final Object targetSpec = targetSpecRaw;
        
        Object typeSpecRaw = raw.get("type");
        if (typeSpecRaw == null) typeSpecRaw = raw.get("mount_type");
        final Object typeSpec = typeSpecRaw;
        
        return ctx -> {
            List<Entity> resolvedPassengers = Resolvers.entities(ctx, targetSpec);
            final List<Entity> passengers;
            if (resolvedPassengers.isEmpty()) {
                if (targetSpec == null && ctx.subject() != null) {
                    passengers = List.of(ctx.subject());
                } else {
                    return ActionResult.ALLOW;
                }
            } else {
                passengers = resolvedPassengers;
            }

            EntityType et = Resolvers.resolveEntityType(typeSpec);
            if (et == null) return ActionResult.ALLOW;

            final EntityType finalEt = et;

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity p : passengers) {
                    Location loc = p.getLocation();
                    Entity mount = loc.getWorld().spawnEntity(loc, finalEt);
                    mount.addPassenger(p);
                }
            });

            return ActionResult.ALLOW;
        };
    }
}
