package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.Map;

import net.rollanddeath.smp.core.scripting.ActionResult;

final class GetBlockAction {
    private GetBlockAction() {}

    static void register() {
        ActionRegistrar.register("get_block", GetBlockAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String storeKey = Resolvers.string(null, raw, "store", "var", "key");
        Object locationSpec = raw.get("location");
        if (locationSpec == null) locationSpec = raw.get("loc");

        final String finalStoreKey = storeKey;
        final Object finalLocationSpec = locationSpec;

        return ctx -> {
            if (finalStoreKey == null) return ActionResult.ALLOW;

            Location loc = Resolvers.location(ctx, finalLocationSpec);
            if (loc == null && ctx.location() != null) {
                loc = ctx.location();
            }

            if (loc != null) {
                Block block = loc.getBlock();
                ctx.setGenericVarCompat(finalStoreKey, block);
            }
            return ActionResult.ALLOW;
        };
    }
}
