package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.DyeColor;
import org.bukkit.entity.Shulker;

final class SetShulkerColorAction {
    private SetShulkerColorAction() {}

    static void register() {
        ActionRegistrar.register("set_shulker_color", SetShulkerColorAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String colorName = Resolvers.string(null, raw, "color");
        if (colorName == null || colorName.isBlank()) return null;
        
        return ctx -> {
            Shulker shulker = ctx.subjectOrEventEntity(Shulker.class);
            if (shulker == null) return ActionResult.ALLOW;
            
            DyeColor color;
            try {
                color = DyeColor.valueOf(colorName.toUpperCase());
            } catch (Exception e) {
                return ActionResult.ALLOW;
            }
            
            ActionUtils.runSync(ctx.plugin(), () -> shulker.setColor(color));
            return ActionResult.ALLOW;
        };
    }
}
