package net.rollanddeath.smp.core.scripting.builtin.actions;

import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.Location;
import org.bukkit.entity.Player;

final class SetVarTargetBlockLocationAction {
    private SetVarTargetBlockLocationAction() {
    }

    static void register() {
        ActionRegistrar.register("set_var_target_block_location", SetVarTargetBlockLocationAction::parse, "raycast_block_location");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        Object keySpec = Resolvers.plain(raw, "key", "store_key", "var");
        Object rangeSpec = Resolvers.plain(raw, "range", "max_distance", "distance");
        Object fallbackSpec = Resolvers.plain(raw, "fallback_distance", "fallback");
        Object yOffsetSpec = Resolvers.plain(raw, "y_offset", "y");
        Object centerSpec = Resolvers.plain(raw, "center_block");
        if (keySpec == null) return null;
        return ctx -> execute(ctx, keySpec, rangeSpec, fallbackSpec, yOffsetSpec, centerSpec);
    }

    private static ActionResult execute(ScriptContext ctx, Object keySpec, Object rangeSpec, Object fallbackSpec, Object yOffsetSpec, Object centerSpec) {
        Player player = ctx.player();
        if (player == null) return ActionResult.ALLOW;

        String key = Resolvers.string(ctx, keySpec);
        if (key == null || key.isBlank()) return ActionResult.ALLOW;

        Integer rangeVal = Resolvers.integer(ctx, rangeSpec);
        Double fallbackVal = Resolvers.doubleVal(ctx, fallbackSpec);
        Double yOffVal = Resolvers.doubleVal(ctx, yOffsetSpec);
        Boolean center = Resolvers.bool(ctx, centerSpec);

        int range = rangeVal != null ? Math.max(1, rangeVal) : 50;
        double fallback = fallbackVal != null ? Math.max(0.0, fallbackVal) : 8.0;
        double yOff = yOffVal != null ? yOffVal : 0.0;
        boolean centerBlock = center != null ? center : true;

        Location out;
        try {
            org.bukkit.block.Block target;
            try {
                target = player.getTargetBlockExact(range);
            } catch (NoSuchMethodError ignored) {
                target = player.getTargetBlock(null, range);
            }

            if (target != null && target.getType() != null && target.getType() != org.bukkit.Material.AIR) {
                out = target.getLocation().clone();
                if (centerBlock) {
                    out.add(0.5, 0.5, 0.5);
                }
            } else {
                out = player.getEyeLocation().clone().add(player.getEyeLocation().getDirection().multiply(fallback));
            }

            out.add(0, yOff, 0);
        } catch (Exception ignored) {
            out = player.getEyeLocation();
        }

        ctx.setGenericVarCompat(key, out);
        return ActionResult.ALLOW;
    }
}