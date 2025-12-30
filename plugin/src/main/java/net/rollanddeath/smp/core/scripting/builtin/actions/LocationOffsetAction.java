package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.Location;

/** Offsets a location and stores the result. */
public final class LocationOffsetAction {
    private LocationOffsetAction() {
    }

    static void register() {
        ActionRegistrar.register("location_offset", LocationOffsetAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object from = firstNonNull(raw, "from", "where", "location", "from_key", "key");
        String storeKey = Resolvers.string(null, raw, "store_key", "to", "key_out", "out");
        if (from == null || storeKey == null || storeKey.isBlank()) return null;

        Double dx = Resolvers.doubleVal(null, raw, "dx");
        Double dy = Resolvers.doubleVal(null, raw, "dy");
        Double dz = Resolvers.doubleVal(null, raw, "dz");
        if (dx == null) dx = Resolvers.doubleVal(null, raw, "x");
        if (dy == null) dy = Resolvers.doubleVal(null, raw, "y");
        if (dz == null) dz = Resolvers.doubleVal(null, raw, "z");

        double ox = dx != null ? dx : 0.0;
        double oy = dy != null ? dy : 0.0;
        double oz = dz != null ? dz : 0.0;

        return ctx -> execute(ctx, from, ox, oy, oz, storeKey.trim());
    }

    private static ActionResult execute(ScriptContext ctx, Object fromSpec, double dx, double dy, double dz, String storeKey) {
        var plugin = ctx.plugin();
        if (plugin == null) return ActionResult.ALLOW;

        Location base = Resolvers.location(fromSpec, ctx, ctx.player() != null ? ctx.player().getWorld() : null);
        if (base == null) return ActionResult.ALLOW;

        Location out = base.clone().add(dx, dy, dz);
        try {
            ctx.setGenericVarCompat(storeKey, out);
        } catch (Exception ignored) {
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
