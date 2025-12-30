package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

final class SpawnAction {
    private SpawnAction() {}

    static void register() {
        ActionRegistrar.register("spawn", SpawnAction::parse, "spawn_entity", "spawn_mob");
    }

    private static Action parse(Map<?, ?> raw) {
        Object locationSpec = raw.get("location");
        if (locationSpec == null) locationSpec = raw.get("at");
        if (locationSpec == null) locationSpec = raw.get("where");
        
        Object typeSpec = raw.get("type");
        if (typeSpec == null) typeSpec = raw.get("entity_type");
        
        Integer amount = Resolvers.integer(null, raw, "amount", "count");
        Double spread = Resolvers.doubleVal(null, raw, "spread", "radius");
        
        Double offsetX = Resolvers.doubleVal(null, raw, "offset_x", "dx");
        Double offsetY = Resolvers.doubleVal(null, raw, "offset_y", "dy");
        Double offsetZ = Resolvers.doubleVal(null, raw, "offset_z", "dz");
        
        List<Action> onSpawn = Resolvers.parseActionList(raw.get("on_spawn"));
        String storeKey = Resolvers.string(null, raw, "store", "var", "key");

        return ctx -> {
            Location loc = Resolvers.location(locationSpec, ctx);
            if (loc == null && ctx.location() != null) loc = ctx.location();
            if (loc == null) return ActionResult.ALLOW;
            
            EntityType et = Resolvers.resolveEntityType(typeSpec);
            if (et == null) return ActionResult.ALLOW;

            int count = amount != null ? Math.max(1, amount) : 1;
            double r = spread != null ? Math.max(0, spread) : 0;
            
            double ox = offsetX != null ? offsetX : 0;
            double oy = offsetY != null ? offsetY : 0;
            double oz = offsetZ != null ? offsetZ : 0;
            
            List<Entity> spawned = new ArrayList<>();
            final Location baseLoc = loc.clone().add(ox, oy, oz);

            ActionUtils.runSync(ctx.plugin(), () -> {
                World w = baseLoc.getWorld();
                if (w == null) return;

                for (int i = 0; i < count; i++) {
                    Location spawnLoc = baseLoc.clone();
                    if (r > 0) {
                        double dx = (ThreadLocalRandom.current().nextDouble() * 2 - 1) * r;
                        double dz = (ThreadLocalRandom.current().nextDouble() * 2 - 1) * r;
                        spawnLoc.add(dx, 0, dz);
                    }
                    
                    try {
                        Entity e = w.spawnEntity(spawnLoc, et);
                        spawned.add(e);
                        
                        if (onSpawn != null && !onSpawn.isEmpty()) {
                            ScriptContext child = new ScriptContext(ctx.plugin(), ctx.player(), ctx.subjectId(), ctx.phase(), ctx.variables());
                            // Expose spawned entity as SUBJECT/TARGET? Or just a var?
                            // Usually SUBJECT is the one running the script. 
                            // Let's expose it as "spawned_entity" and also potentially override SUBJECT if needed, 
                            // but safer to just use a var or let the actions use "spawned_entity".
                            // Actually, for "on_spawn", the subject IS usually the new entity.
                            // But we can't easily change the subject type if it was a Player context.
                            // Let's try to set it as a variable "entity" or "spawned".
                            child.setGenericVarCompat("entity", e);
                            child.setGenericVarCompat("spawned", e);
                            
                            // If we want to run actions AS the entity, we'd need a new context with that entity as subject.
                            // But ScriptContext expects a Player usually. 
                            // Let's just run it with the variables.
                            ScriptEngine.runAllWithResult(child, onSpawn);
                        }
                    } catch (Exception ignored) {}
                }
            });

            if (storeKey != null) {
                if (count == 1 && !spawned.isEmpty()) {
                    ctx.setGenericVarCompat(storeKey, spawned.get(0));
                } else {
                    ctx.setGenericVarCompat(storeKey, spawned);
                }
            }

            return ActionResult.ALLOW;
        };
    }
}
