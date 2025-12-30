package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

final class SetMobMaxHealthAction {
    private SetMobMaxHealthAction() {}

    static void register() {
        ActionRegistrar.register("set_mob_max_health", SetMobMaxHealthAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        Double value = Resolvers.doubleVal(null, raw, "value");
        String valueKey = Resolvers.string(null, raw, "value_key");
        if (value == null && (valueKey == null || valueKey.isBlank())) return null;

        return ctx -> {
            LivingEntity entity = ctx.subjectOrEventEntity(LivingEntity.class);
            if (entity == null) return ActionResult.ALLOW;

            double val;
            if (value != null) {
                val = value;
            } else {
                Double d = Resolvers.resolveDouble(ctx.getValue(valueKey));
                if (d == null) return ActionResult.ALLOW;
                val = d;
            }

            ActionUtils.runSync(ctx.plugin(), () -> {
                AttributeInstance inst = entity.getAttribute(Attribute.MAX_HEALTH);
                if (inst != null) {
                    inst.setBaseValue(val);
                    entity.setHealth(val);
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
