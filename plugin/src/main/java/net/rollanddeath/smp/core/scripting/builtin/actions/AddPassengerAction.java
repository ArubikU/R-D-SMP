package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Entity;

final class AddPassengerAction {
    private AddPassengerAction() {}

    static void register() {
        ActionRegistrar.register("add_passenger", AddPassengerAction::parse, "set_passenger", "mount");
    }

    private static Action parse(Map<?, ?> raw) {
        Object vehicleSpec = raw.get("vehicle");
        if (vehicleSpec == null) vehicleSpec = raw.get("target");
        
        Object passengerSpec = raw.get("passenger");
        if (passengerSpec == null) passengerSpec = raw.get("entity");
        
        if (vehicleSpec == null && passengerSpec == null) return null;

        final Object finalVehicleSpec = vehicleSpec;
        final Object finalPassengerSpec = passengerSpec;

        return ctx -> {
            List<Entity> vehicles = Resolvers.entities(ctx, finalVehicleSpec);
            List<Entity> passengers = Resolvers.entities(ctx, finalPassengerSpec);
            
            if (vehicles.isEmpty() && ctx.subject() != null) vehicles = List.of(ctx.subject());
            if (passengers.isEmpty() && ctx.subject() != null) passengers = List.of(ctx.subject());
            
            if (vehicles.isEmpty() || passengers.isEmpty()) return ActionResult.ALLOW;

            final List<Entity> finalVehicles = vehicles;
            final List<Entity> finalPassengers = passengers;

            ActionUtils.runSync(ctx.plugin(), () -> {
                // If 1 vehicle and N passengers -> add all passengers to vehicle
                // If N vehicles and 1 passenger -> add passenger to first vehicle (can't be in multiple)
                // If N vehicles and N passengers -> 1:1 mapping?
                
                if (finalVehicles.size() == 1) {
                    Entity v = finalVehicles.get(0);
                    for (Entity p : finalPassengers) {
                        if (v != p) v.addPassenger(p);
                    }
                } else if (finalPassengers.size() == 1) {
                    Entity p = finalPassengers.get(0);
                    Entity v = finalVehicles.get(0);
                    if (v != p) v.addPassenger(p);
                } else {
                    // 1:1
                    int count = Math.min(finalVehicles.size(), finalPassengers.size());
                    for (int i = 0; i < count; i++) {
                        Entity v = finalVehicles.get(i);
                        Entity p = finalPassengers.get(i);
                        if (v != p) v.addPassenger(p);
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }
}
