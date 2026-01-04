package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

final class SpawnPassengerAction {
    private SpawnPassengerAction() {}

    static void register() {
        ActionRegistrar.register("spawn_passenger", SpawnPassengerAction::parse, "spawn_passenger_for_mob");
    }

    private static Action parse(Map<?, ?> raw) {
        Object targetSpecRaw = raw.get("target"); // The vehicle
        if (targetSpecRaw == null) targetSpecRaw = raw.get("vehicle");
        if (targetSpecRaw == null) targetSpecRaw = raw.get("mob");
        final Object targetSpec = targetSpecRaw;
        
        Object typeSpecRaw = raw.get("type");
        if (typeSpecRaw == null) typeSpecRaw = raw.get("passenger_type");
        final Object typeSpec = typeSpecRaw;
        
        return ctx -> {
            List<Entity> resolvedVehicles = Resolvers.entities(ctx, targetSpec);
            final List<Entity> vehicles;
            if (resolvedVehicles.isEmpty()) {
                if (targetSpec == null && ctx.subject() != null) {
                    vehicles = List.of(ctx.subject());
                } else {
                    return ActionResult.ALLOW;
                }
            } else {
                vehicles = resolvedVehicles;
            }

            EntityType et = Resolvers.resolveEntityType(typeSpec);
            if (et == null) return ActionResult.ALLOW;

            final EntityType finalEt = et;

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity v : vehicles) {
                    Location loc = v.getLocation();
                    Entity passenger = loc.getWorld().spawnEntity(loc, finalEt);
                    v.addPassenger(passenger);
                }
            });

            return ActionResult.ALLOW;
        };
    }
}
