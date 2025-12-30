package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

final class ExplodeNearbyEntitiesAction {
    private ExplodeNearbyEntitiesAction() {}

    static void register() {
        ActionRegistrar.register("explode_nearby_entities", ExplodeNearbyEntitiesAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String entityType = Resolvers.string(null, raw, "entity_type");
        Object entityTypesObj = raw.get("entity_types");
        List<String> entityTypes = null;
        if (entityTypesObj instanceof List<?> list && !list.isEmpty()) {
            entityTypes = list.stream().filter(String.class::isInstance).map(String.class::cast).toList();
        }
        if ((entityType == null || entityType.isBlank()) && (entityTypes == null || entityTypes.isEmpty())) return null;

        Double radius = Resolvers.doubleVal(null, raw, "radius");
        Double power = Resolvers.doubleVal(null, raw, "power");
        boolean setFire = raw.get("set_fire") instanceof Boolean b ? b : false;
        boolean breakBlocks = raw.get("break_blocks") instanceof Boolean b ? b : false;

        Set<String> types = (entityTypes != null ? entityTypes : List.of(entityType)).stream()
            .map(s -> s.trim().toUpperCase(Locale.ROOT))
            .collect(Collectors.toSet());
        
        double r = radius != null ? Math.max(0.0, radius) : 3.0;
        float p = power != null ? power.floatValue() : 2.0f;

        return ctx -> {
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            
            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity e : player.getNearbyEntities(r, r, r)) {
                    if (types.contains(e.getType().name())) {
                        e.getWorld().createExplosion(e.getLocation(), p, setFire, breakBlocks);
                        e.remove();
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
